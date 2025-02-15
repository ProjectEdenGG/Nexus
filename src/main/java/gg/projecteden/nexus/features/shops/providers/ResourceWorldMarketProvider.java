package gg.projecteden.nexus.features.shops.providers;

import gg.projecteden.api.common.utils.EnumUtils.IterableEnum;
import gg.projecteden.api.common.utils.StringUtils;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.features.shops.Market;
import gg.projecteden.nexus.features.shops.providers.common.ShopProvider;
import gg.projecteden.nexus.models.shop.Shop;
import gg.projecteden.nexus.models.shop.Shop.Product;
import gg.projecteden.nexus.models.shop.ShopService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.SoundBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Title("Resource World Market Config")
public class ResourceWorldMarketProvider extends ShopProvider {
	private static final ShopService service = new ShopService();
	private static final Shop market = service.getMarket();

	public ResourceWorldMarketProvider(ShopProvider previousMenu) {
		this.previousMenu = previousMenu;
	}

	@Override
	public void init() {
		super.init();

		final Shop shop = service.get(viewer);
		final AutoSellBehavior autoSellBehavior = shop.getResourceMarketAutoSellBehavior();
		final List<Product> products = Market.RESOURCE_WORLD_PRODUCTS;

		contents.set(0, 8, ClickableItem.of(autoSellBehavior.getSettingItem(), e -> {
				shop.setResourceMarketAutoSellBehavior(autoSellBehavior.nextWithLoop());
				service.save(shop);
				open(viewer, contents.pagination().getPage());
			}
		));

		List<ClickableItem> items = new ArrayList<>();

		products.forEach(product -> {
			final Material type = product.getItem().getType();
			final ItemBuilder builder = product.getItemWithLore().lore(product.getExchange().getLore()).lore("");
			Runnable _toggle = null;

			switch (autoSellBehavior) {
				case DISABLE_ALL -> builder.lore("&c&lDisabled").lore("&e&oOverridden by Auto Sell Behavior").glow();
				case ENABLE_ALL -> builder.lore("&a&lEnabled").lore("&e&oOverridden by Auto Sell Behavior");
				default -> {
					if (shop.getDisabledResourceMarketItems().contains(type)) {
						builder.lore("&c&lDisabled").lore("&a&oClick to enable").glow();
						_toggle = () -> shop.getDisabledResourceMarketItems().remove(type);
					} else {
						builder.lore("&a&lEnabled").lore("&c&oClick to disable");
						_toggle = () -> shop.getDisabledResourceMarketItems().add(type);
					}
				}
			}

			final ItemStack item = builder.build();
			final Runnable toggle = _toggle;
			items.add(ClickableItem.of(item, e -> {
				if (toggle != null) {
					new SoundBuilder(Sound.UI_BUTTON_CLICK).receiver(viewer).play();
					toggle.run();
					service.save(shop);
				}

				open(viewer, contents.pagination().getPage());
			}));
		});

		paginate(items);
	}

	@Getter
	@AllArgsConstructor
	public enum AutoSellBehavior implements IterableEnum {
		DISABLE_ALL("&3&oDisables all items, overrides individual toggles"),
		ENABLE_ALL("&3&oEnables all items, overrides individual toggles"),
		INDIVIDUAL("&3&oClick items to toggle them"),
		;

		private final String description;

		public ItemBuilder getSettingItem() {
			return new ItemBuilder(ItemModelType.GUI_GEAR).name("&6Auto Sell Behavior: ")
				.lore("&7⬇ " + StringUtils.camelCase(this.previousWithLoop().name()))
				.lore("&e⬇ " + StringUtils.camelCase(this.name()))
				.lore("&7⬇ " + StringUtils.camelCase(this.nextWithLoop().name()))
				.lore("")
				.lore(description);
		}
	}

}
