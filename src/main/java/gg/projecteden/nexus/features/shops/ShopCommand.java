package gg.projecteden.nexus.features.shops;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.nexus.features.economy.commands.TransactionsCommand;
import gg.projecteden.nexus.features.shops.providers.BrowseProductsProvider;
import gg.projecteden.nexus.features.shops.providers.BrowseShopsProvider;
import gg.projecteden.nexus.features.shops.providers.EditProductProvider;
import gg.projecteden.nexus.features.shops.providers.MainMenuProvider;
import gg.projecteden.nexus.features.shops.providers.PlayerShopProvider;
import gg.projecteden.nexus.features.shops.providers.YourShopProvider;
import gg.projecteden.nexus.features.shops.providers.YourShopProvider.CollectItemsProvider;
import gg.projecteden.nexus.features.shops.providers.common.ShopMenuFunctions.FilterSearchType;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromHelp;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleteIgnore;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.banker.Transaction;
import gg.projecteden.nexus.models.banker.Transaction.TransactionCause;
import gg.projecteden.nexus.models.banker.Transactions;
import gg.projecteden.nexus.models.shop.Shop;
import gg.projecteden.nexus.models.shop.Shop.Product;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.models.shop.ShopService;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Utils.ActionGroup;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import world.bentobox.bentobox.api.events.island.IslandResetEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

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
	@Description("Open the shops menu")
	void run() {
		new MainMenuProvider(null).open(player());
	}

	@Path("edit")
	@Description("Edit your shop")
	void edit() {
		new YourShopProvider(null).open(player());
	}

	@Path("<player>")
	@Description("Open a player's shop")
	void player(Shop shop) {
		if (shop.getProducts(shopGroup).isEmpty())
			error("No items in " + shop.getName() + "'s " + camelCase(shopGroup) + " shop");
		new PlayerShopProvider(null, shop).open(player());
	}

	@Path("search <item...>")
	@Description("Search the shop for an item")
	void search(@Arg(tabCompleter = ItemStack.class) String text) {
		new BrowseProductsProvider(null, FilterSearchType.SEARCH.of(StringUtils.stripColor(text))).open(player());
	}

	@Path("items")
	@Description("Browse all available items on the store")
	void items() {
		new BrowseProductsProvider(null).open(player());
	}

	@Path("list")
	@Description("List available shops")
	void list() {
		new BrowseShopsProvider(null).open(player());
	}

	@Path("collect")
	@Description("Collect items sold to you or items that didnt fit in your inventory")
	void collect() {
		new CollectItemsProvider(player(), null);
	}

	@Path("massRestock")
	@Description("Restock any item in your shop in one menu")
	void massRestock() {
		new EditProductProvider.MassAddStockProvider(player(), null, service.get(player()));
	}

	@Async
	@Path("history [player] [page] [--world]")
	@Description("View your shop transaction history")
	void history(@Arg("self") Transactions banker, @Arg("1") int page, @Switch @Arg("current") ShopGroup world) {
		List<Transaction> transactions = new ArrayList<>(banker.getTransactions())
				.stream().filter(transaction ->
						transaction.getShopGroup() == world &&
						(transaction.getCause() == TransactionCause.SHOP_PURCHASE && banker.getUuid().equals(transaction.getSender())) ||
						(transaction.getCause() == TransactionCause.SHOP_SALE && banker.getUuid().equals(transaction.getReceiver())))
				.sorted(Comparator.comparing(Transaction::getTimestamp).reversed())
				.collect(Collectors.toList());

		if (transactions.isEmpty())
			error("&cNo transactions found");

		send("");
		send(PREFIX + camelCase(world) + " history" + (isSelf(banker) ? "" : " for &e" + banker.getName()));

		BiFunction<Transaction, String, JsonBuilder> formatter = TransactionsCommand.getFormatter(player(), banker);

		new Paginator<Transaction>()
			.values(Transaction.combine(transactions))
			.formatter(formatter)
			.command("/shop history " + banker.getName() + " --world=" + world.name().toLowerCase())
			.page(page)
			.send();
	}

	@Getter
	private static final Map<UUID, Product> interactStockMap = new HashMap<>();

	@HideFromWiki
	@HideFromHelp
	@TabCompleteIgnore
	@Path("cancelInteractStock")
	void cancelInteractStock() {
		if (!interactStockMap.containsKey(uuid()))
			error("You are not stocking any items");

		Product product = interactStockMap.get(uuid());
		interactStockMap.remove(uuid());
		send(PREFIX + "Stopped stocking " + StringUtils.pretty(product.getItem()));
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
			if (ItemUtils.isSimilar(product.getItem(), content)) {
				stockToAdd += content.getAmount();
				content.setAmount(0);
			}
		}

		product.addStock(stockToAdd);
		service.save(product.getShop());

		send(event.getPlayer(), new JsonBuilder(Shops.PREFIX + "Added &e" + stockToAdd + " &3stock to "
				+ StringUtils.pretty(product.getItem()) + " (&e" + product.getStock() + " &3total). &eClick here to end")
				.command("/shop cancelInteractStock"));
	}

	@EventHandler
	public void on(IslandResetEvent event) {
		try {
			final ShopGroup shopGroup = switch (event.getIsland().getGameMode().toUpperCase()) {
				case "AONEBLOCK" -> ShopGroup.ONEBLOCK;
				case "BSKYBLOCK" -> ShopGroup.SKYBLOCK;
				default -> throw new InvalidInputException("Unknown island gamemode " + event.getIsland().getGameMode());
			};

			service.edit(event.getPlayerUUID(), shop -> shop.getProducts().removeIf(product -> product.getShopGroup() == shopGroup));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
