package gg.projecteden.nexus.features.shops.providers;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.shops.Market;
import gg.projecteden.nexus.features.shops.providers.common.ShopProvider;
import gg.projecteden.nexus.models.shop.Shop;
import gg.projecteden.nexus.models.shop.Shop.Product;
import gg.projecteden.nexus.models.shop.ShopService;
import gg.projecteden.nexus.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Title("Resource World Market")
public class ResourceWorldMarketProvider extends ShopProvider {
	private static final ShopService service = new ShopService();
	private static final Shop market = service.getMarket();

	public ResourceWorldMarketProvider(ShopProvider previousMenu) {
		this.previousMenu = previousMenu;
	}

	@Override
	public void init() {
		super.init();

		final Shop shop = service.get(player);
		final List<Product> products = Market.RESOURCE_WORLD_PRODUCTS;

		final ItemBuilder info = new ItemBuilder(Material.BOOK).name("&6&lInfo")
			.lore("Items are automatically sold when collected");
		contents.set(0, 4, ClickableItem.empty(info.build()));

		List<ClickableItem> items = new ArrayList<>();

		products.forEach(product -> {
			final Material type = product.getItem().getType();
			final ItemBuilder builder = product.getItemWithLore().lore(product.getExchange().getLore()).lore("");
			final Runnable toggle;

			if (shop.getDisabledResourceMarketItems().contains(type)) {
				builder.lore("&c&lDisabled").lore("&aClick to enable").glow();
				toggle = () -> shop.getDisabledResourceMarketItems().remove(type);
			} else {
				builder.lore("&a&lEnabled").lore("&cClick to disable");
				toggle = () -> shop.getDisabledResourceMarketItems().add(type);
			}

			final ItemStack item = builder.build();
			items.add(ClickableItem.of(item, e -> {
				toggle.run();
				service.save(shop);
				open(player, contents.pagination().getPage());
			}));
		});

		paginator().items(items).build();
	}

}
