package gg.projecteden.nexus.features.shops.providers;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.menus.MenuUtils.ConfirmationMenu;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.TemporaryMenuListener;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.shops.Shops;
import gg.projecteden.nexus.features.shops.providers.common.ShopProvider;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.shop.Shop;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.models.shop.ShopService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Title("&0Your shop")
public class YourShopProvider extends ShopProvider {

	public YourShopProvider(ShopProvider previousMenu) {
		this.previousMenu = previousMenu;
	}

	@Override
	public void init() {
		super.init();

		Shop shop = new ShopService().get(viewer);

		contents.set(0, 1, ClickableItem.of(Material.ENDER_EYE, "&6Preview your shop", e -> new PlayerShopProvider(this, shop).open(viewer)));

		ItemBuilder description = new ItemBuilder(Material.OAK_SIGN).name("&6Set shop description");
		if (!shop.getDescription().isEmpty())
			description.lore("").lore(shop.getDescription());

		contents.set(0, 2, ClickableItem.of(description.build(), e -> Nexus.getSignMenuFactory()
			.lines(shop.getDescriptionArray())
			.prefix(Shops.PREFIX)
			.colorize(false)
			.response(lines -> {
				shop.setDescription(Arrays.asList(lines));
				service.save(shop);
				open(viewer);
			}).open(viewer)));

		contents.set(0, 4, ClickableItem.of(Material.LIME_CONCRETE_POWDER, "&6Add item", e -> new ExchangeConfigProvider(this).open(viewer)));

		contents.set(0, 5, ClickableItem.of(Material.HOPPER, "&6Mass Restock", e -> new EditProductProvider.MassAddStockProvider(viewer, this, shop)));

		contents.set(0, 6, ClickableItem.of(Material.WRITABLE_BOOK, "&6Shop history", e -> {
			PlayerUtils.runCommand(viewer, "shop history");
			viewer.closeInventory();
		}));
		contents.set(0, 7, ClickableItem.of(Material.CYAN_SHULKER_BOX, "&6Collect items", e -> new CollectItemsProvider(viewer, this)));

		contents.set(5, 3, ClickableItem.of(new ItemBuilder(Material.RED_CONCRETE_POWDER).name("&cDisable all").lore("", "&7Click to disable all items"), e3 ->
			ConfirmationMenu.builder()
				.onConfirm(e21 -> {
					shop.getProducts().forEach(product2 -> product2.setEnabled(false));
					service.save(shop);
					open(viewer, page);
				})
				.onCancel(e21 -> open(viewer, page))
				.open(viewer)));
		contents.set(5, 5, ClickableItem.of(new ItemBuilder(Material.LIME_CONCRETE_POWDER).name("&aEnable all").lore("", "&7Click to enable all items"), e1 ->
			ConfirmationMenu.builder()
				.onConfirm(e2 -> {
					shop.getProducts().forEach(product1 -> product1.setEnabled(true));
					service.save(shop);
					open(viewer, page);
				})
				.onCancel(e2 -> open(viewer, page))
				.open(viewer)));

		if (shop.getProducts() == null || shop.getProducts().size() == 0) return;
		List<ClickableItem> items = new ArrayList<>();

		shop.getProducts(shopGroup).forEach(product -> {
			ItemStack item = product.getItemWithOwnLore().build();
			items.add(ClickableItem.of(item, e -> {
				if (handleRightClick(product, e))
					return;
				new EditProductProvider(this, product).open(viewer);
			}));
		});

		paginate(items);
	}

	@Title("Collect Items")
	public static class CollectItemsProvider implements TemporaryMenuListener {
		@Getter
		private final Player player;
		private final ShopProvider previousMenu;

		public CollectItemsProvider(Player player, ShopProvider previousMenu) {
			this.player = player;
			this.previousMenu = previousMenu;

			ShopService service = new ShopService();
			Shop shop = service.get(player);

			if (shop.getHolding().isEmpty())
				throw new InvalidInputException("No items available for collection");

			List<ItemStack> items = new ArrayList<>();
			final int max = Math.min(54, shop.getHolding(ShopGroup.of(player)).size());
			final Iterator<ItemStack> iterator = shop.getHolding(ShopGroup.of(player)).iterator();
			while (items.size() < max && iterator.hasNext()) {
				items.add(iterator.next());
				iterator.remove();
			}
			service.save(shop);

			open(items);
		}

		@Override
		public void onClose(InventoryCloseEvent event, List<ItemStack> contents) {
			ShopService service = new ShopService();
			Shop shop = service.get(player);

			for (ItemStack content : event.getInventory().getContents())
				if (!Nullables.isNullOrAir(content))
					shop.addHolding(ShopGroup.of(player), content);

			service.save(shop);

			if (previousMenu != null)
				Tasks.wait(1, () -> previousMenu.open(player));
		}
	}

}
