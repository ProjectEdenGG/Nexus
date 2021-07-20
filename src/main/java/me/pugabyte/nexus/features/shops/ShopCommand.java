package me.pugabyte.nexus.features.shops;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.features.shops.ShopMenuFunctions.FilterSearchType;
import me.pugabyte.nexus.features.shops.providers.BrowseProductsProvider;
import me.pugabyte.nexus.features.shops.providers.BrowseShopsProvider;
import me.pugabyte.nexus.features.shops.providers.MainMenuProvider;
import me.pugabyte.nexus.features.shops.providers.PlayerShopProvider;
import me.pugabyte.nexus.features.shops.providers.YourShopProvider;
import me.pugabyte.nexus.features.shops.providers.YourShopProvider.CollectItemsProvider;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Async;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.banker.Transaction;
import me.pugabyte.nexus.models.banker.Transaction.TransactionCause;
import me.pugabyte.nexus.models.banker.Transactions;
import me.pugabyte.nexus.models.shop.Shop;
import me.pugabyte.nexus.models.shop.Shop.Product;
import me.pugabyte.nexus.models.shop.Shop.ShopGroup;
import me.pugabyte.nexus.models.shop.ShopService;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Utils.ActionGroup;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static me.pugabyte.nexus.features.economy.commands.TransactionsCommand.getFormatter;
import static me.pugabyte.nexus.models.banker.Transaction.combine;
import static me.pugabyte.nexus.utils.ItemUtils.isSimilar;
import static me.pugabyte.nexus.utils.StringUtils.pretty;
import static me.pugabyte.nexus.utils.StringUtils.stripColor;

@Aliases("shops")
@NoArgsConstructor
public class ShopCommand extends CustomCommand implements Listener {
	private final ShopService service = new ShopService();
	private ShopGroup shopGroup;

	public ShopCommand(@NonNull CommandEvent event) {
		super(event);
		if (isCommandEvent()) {
			shopGroup = ShopGroup.of(world());
			if (shopGroup == null)
				error("Shops are not enabled in this world");
		}
	}

	@Path
	void run() {
		new MainMenuProvider(null).open(player());
	}

	@Path("edit")
	void edit() {
		new YourShopProvider(null).open(player());
	}

	@Path("<player>")
	void player(Shop shop) {
		if (shop.getProducts(shopGroup).isEmpty())
			error("No items in " + shop.getName() + "'s " + camelCase(shopGroup) + " shop");
		new PlayerShopProvider(null, shop).open(player());
	}

	@Path("search <item...>")
	void search(@Arg(tabCompleter = Material.class) String text) {
		new BrowseProductsProvider(null, FilterSearchType.SEARCH.of(stripColor(text))).open(player());
	}

	@Path("items")
	void items() {
		new BrowseProductsProvider(null).open(player());
	}

	@Path("list")
	void list() {
		new BrowseShopsProvider(null).open(player());
	}

	@Path("collect")
	void collect() {
		new CollectItemsProvider(null).open(player());
	}

	@Async
	@Path("history [player] [page]")
	void history(@Arg("self") Transactions banker, @Arg("1") int page) {
		List<Transaction> transactions = new ArrayList<>(banker.getTransactions())
				.stream().filter(transaction ->
						transaction.getShopGroup() == shopGroup &&
						(transaction.getCause() == TransactionCause.SHOP_PURCHASE && banker.getUuid().equals(transaction.getSender())) ||
						(transaction.getCause() == TransactionCause.SHOP_SALE && banker.getUuid().equals(transaction.getReceiver())))
				.sorted(Comparator.comparing(Transaction::getTimestamp).reversed())
				.collect(Collectors.toList());

		if (transactions.isEmpty())
			error("&cNo transactions found");

		send("");
		send(PREFIX + camelCase(shopGroup) + " history" + (isSelf(banker) ? "" : " for &e" + banker.getName()));

		BiFunction<Transaction, String, JsonBuilder> formatter = getFormatter(player(), banker);

		paginate(combine(transactions), formatter, "/shop history " + banker.getName(), page);
	}

	@Path("cleanup")
	@Permission("group.admin")
	void cleanup() {
		Shop shop = service.get(PlayerUtils.getPlayer("LadyAnime"));
		shop.getProducts().removeIf(product -> !product.canFulfillPurchase());
		service.save(shop);
		send(PREFIX + "Cleaned up LadyAnime's shop");
	}

	@Getter
	private static final Map<UUID, Product> interactStockMap = new HashMap<>();

	@Path("cancelInteractStock")
	void cancelInteractStock() {
		if (!interactStockMap.containsKey(uuid()))
			error("You are not stocking any items");

		Product product = interactStockMap.get(uuid());
		interactStockMap.remove(uuid());
		send(PREFIX + "Stopped stocking " + pretty(product.getItem()));
	}

	@EventHandler
	public void onContainerInteract(PlayerInteractEvent event) {
		if (!interactStockMap.containsKey(event.getPlayer().getUniqueId()))
			return;

		Block block = event.getClickedBlock();
		if (!ActionGroup.CLICK_BLOCK.applies(event) || block == null)
			return;

		BlockState state = block.getState();
		if (!(state instanceof Container container))
			return;

		Product product = interactStockMap.get(event.getPlayer().getUniqueId());

		event.setCancelled(true);

		int stockToAdd = 0;
		for (ItemStack content : container.getInventory().getContents()) {
			if (isSimilar(product.getItem(), content)) {
				stockToAdd += content.getAmount();
				content.setAmount(0);
			}
		}

		product.addStock(stockToAdd);
		service.save(product.getShop());

		send(event.getPlayer(), new JsonBuilder(Shops.PREFIX + "Added &e" + stockToAdd + " &3stock to "
				+ pretty(product.getItem()) + " (&e" + product.getStock() + " &3total). &eClick here to end")
				.command("/shop cancelInteractStock"));
	}

}
