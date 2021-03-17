package me.pugabyte.nexus.features.shops;

import lombok.NonNull;
import me.pugabyte.nexus.features.shops.ShopMenuFunctions.FilterSearchType;
import me.pugabyte.nexus.features.shops.providers.BrowseProductsProvider;
import me.pugabyte.nexus.features.shops.providers.MainMenuProvider;
import me.pugabyte.nexus.features.shops.providers.PlayerShopProvider;
import me.pugabyte.nexus.features.shops.providers.SearchProductsProvider;
import me.pugabyte.nexus.features.shops.providers.YourShopProvider;
import me.pugabyte.nexus.features.shops.providers.YourShopProvider.CollectItemsProvider;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Async;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.banker.Banker;
import me.pugabyte.nexus.models.banker.Transaction;
import me.pugabyte.nexus.models.banker.Transaction.TransactionCause;
import me.pugabyte.nexus.models.shop.Shop;
import me.pugabyte.nexus.models.shop.Shop.ShopGroup;
import me.pugabyte.nexus.models.shop.ShopService;
import me.pugabyte.nexus.utils.JsonBuilder;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static me.pugabyte.nexus.features.economy.commands.TransactionsCommand.getFormatter;
import static me.pugabyte.nexus.models.banker.Transaction.combine;
import static me.pugabyte.nexus.utils.StringUtils.stripColor;

@Aliases("shops")
public class ShopCommand extends CustomCommand {
	private final ShopService service = new ShopService();
	private final ShopGroup shopGroup = ShopGroup.get(world());

	public ShopCommand(@NonNull CommandEvent event) {
		super(event);
		if (isCommandEvent()) {
			if (!hasPermission("shops.use"))
				error("Currently in beta testing, coming soon!");
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

	@Path("search <item>")
	void search(@Arg(tabCompleter = Material.class) String text) {
		BrowseProductsProvider provider = new BrowseProductsProvider(null, FilterSearchType.SEARCH.of(stripColor(text), product ->
				SearchProductsProvider.filter(product.getItem(), item -> item.getType().name().toLowerCase().contains(stripColor(text).toLowerCase()))));
		provider.open(player());
	}

	@Path("collect")
	void collect() {
		if (world().getName().startsWith("resource"))
			error("You cannot use player shops while in the resource world");

		new CollectItemsProvider(null).open(player());
	}

	@Async
	@Path("history [player] [page]")
	void history(@Arg("self") Banker banker, @Arg("1") int page) {
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

}
