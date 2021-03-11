package me.pugabyte.nexus.features.shops;

import lombok.NonNull;
import me.pugabyte.nexus.features.shops.ShopMenuFunctions.FilterSearchType;
import me.pugabyte.nexus.features.shops.providers.BrowseItemsProvider;
import me.pugabyte.nexus.features.shops.providers.MainMenuProvider;
import me.pugabyte.nexus.features.shops.providers.PlayerShopProvider;
import me.pugabyte.nexus.features.shops.providers.YourShopProvider;
import me.pugabyte.nexus.features.shops.providers.YourShopProvider.CollectItemsProvider;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.framework.commands.models.events.TabEvent;
import me.pugabyte.nexus.models.shop.Shop;
import me.pugabyte.nexus.models.shop.Shop.ShopGroup;
import me.pugabyte.nexus.models.shop.ShopService;
import org.bukkit.Material;

import static me.pugabyte.nexus.utils.StringUtils.stripColor;

@Aliases("shops")
public class ShopCommand extends CustomCommand {
	private final ShopService service = new ShopService();
	private final ShopGroup shopGroup = ShopGroup.get(world());

	public ShopCommand(@NonNull CommandEvent event) {
		super(event);
		if (!(event instanceof TabEvent)) {
			if (!hasPermission("shops.use"))
				error("&cComing soon!");
			if (shopGroup == null)
				error("Shops are not enabled in this world");
			if (shopGroup == ShopGroup.RESOURCE)
				error("You cannot use player shops while in the resource world");
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
		BrowseItemsProvider provider = new BrowseItemsProvider(null, FilterSearchType.SEARCH.of(stripColor(text), product ->
				product.getItem().getType().name().toLowerCase().contains(stripColor(text).toLowerCase())));
		provider.open(player());
	}

	@Path("collect")
	void collect() {
		new CollectItemsProvider(null).open(player());
	}

}
