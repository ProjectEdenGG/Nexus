package gg.projecteden.nexus.features.shops.providers;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.menus.MenuUtils.ConfirmationMenu;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.SignMenuFactory;
import gg.projecteden.nexus.features.menus.api.TemporaryMenuListener;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.shops.ShopCommand;
import gg.projecteden.nexus.features.shops.Shops;
import gg.projecteden.nexus.features.shops.providers.common.ShopProvider;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.shop.Shop;
import gg.projecteden.nexus.models.shop.Shop.ExchangeType;
import gg.projecteden.nexus.models.shop.Shop.Product;
import gg.projecteden.nexus.models.shop.ShopService;
import gg.projecteden.nexus.utils.*;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Rows(4)
@Title("&0Edit Item")
public class EditProductProvider extends ShopProvider {
	private final Product product;

	public EditProductProvider(ShopProvider previousMenu, Product product) {
		this.previousMenu = previousMenu;
		this.product = product;
	}

	@Override
	public void init() {
		super.init();

		contents.set(0, 4, ClickableItem.of(product.getItemWithOwnLore().build(), e -> new ExchangeConfigProvider(this, product).open(viewer)));
		if (product.getExchangeType() == ExchangeType.BUY) {
			ItemBuilder builder = new ItemBuilder(Material.GOLD_INGOT)
				.name("&6Edit Stock")
				.lore("&7Enter the dollar amount you are")
				.lore("&7willing to spend on this item, or")
				.lore("&7enter -1 to allow unlimited purchases")
				.loreize(false);

			contents.set(1, 4, ClickableItem.of(builder.build(), e -> Nexus.getSignMenuFactory()
				.lines("", SignMenuFactory.ARROWS, "Enter an amount", "or -1 for no limit")
				.prefix(Shops.PREFIX)
				.onError(() -> open(viewer))
				.response(lines -> {
					if (lines[0].length() > 0) {
						String input = lines[0].replaceAll("[^\\d.-]+", "");
						if (!Utils.isDouble(input))
							throw new InvalidInputException("Could not parse &e" + lines[0] + " &cas a dollar amount");
						double stock = new BigDecimal(input).setScale(2, RoundingMode.HALF_UP).doubleValue();
						if (!(stock == -1 || stock >= 0))
							throw new InvalidInputException("Stock must be -1 (unlimited), or $0 or greater");
						product.setStock(stock);
						service.save(product.getShop());
					}
					open(viewer);
				}).open(viewer)
			));
		} else {
			contents.set(1, 3, ClickableItem.of(new ItemBuilder(Material.LIME_CONCRETE_POWDER).name("&6Add Stock").lore("&f", "&7Right click to add in bulk").build(), e -> {
				if (e.isRightClick()) {
					viewer.closeInventory();
					ShopCommand.getInteractStockMap().put(viewer.getUniqueId(), product);
					PlayerUtils.send(viewer, new JsonBuilder(Shops.PREFIX + "Right click any container (ie chest, shulker box, etc) to stock &e"
						+ StringUtils.pretty(product.getItem()) + "&3. &eClick here to end").command("/shop cancelInteractStock"));
				} else {
					new AddStockProvider(viewer, this, product);
				}
			}));
			contents.set(1, 5, ClickableItem.of(Material.RED_CONCRETE_POWDER, "&6Remove Stock", e -> new RemoveStockProvider(viewer, this, product)));
		}

		ItemBuilder purchasable = new ItemBuilder(Material.WHITE_STAINED_GLASS);
		if (product.isPurchasable())
			purchasable.name("&aPurchasable")
				.lore("&7Click to &cdisable &7purchases")
				.lore("&7Item will still show in your shop,")
				.lore("&7but players cannot purchase it.")
				.lore("&7Can be used for shop organization")
				.loreize(false);
		else
			purchasable.name("&cNot purchasable").lore("&7Click to &aenable &7purchases");

		ItemBuilder enabled = new ItemBuilder(Material.LEVER);
		if (product.isEnabled())
			enabled.name("&aEnabled").lore("&7Click to &cdisable&7, hiding").lore("&7the item from public view");
		else
			enabled.name("&cDisabled").lore("&7Click to &aenable&7, allowing others").lore("&7to view and purchase the item");

		contents.set(3, 2, ClickableItem.of(purchasable.build(), e -> {
			product.setPurchasable(!product.isPurchasable());
			service.save(product.getShop());
			open(viewer);
		}));

		contents.set(3, 4, ClickableItem.of(enabled.build(), e -> {
			product.setEnabled(!product.isEnabled());
			service.save(product.getShop());
			open(viewer);
		}));

		contents.set(3, 6, ClickableItem.of(new ItemBuilder(Material.LAVA_BUCKET).name("&cDelete").build(), e ->
			ConfirmationMenu.builder()
				.onConfirm(e2 -> {
					Shop shop = service.get(viewer);
					shop.removeProduct(product);
					service.save(shop);
					previousMenu.open(viewer);
				})
				.onCancel(e2 -> open(viewer))
				.open(viewer)));
	}

	@Title("&0Add Stock")
	public static class AddStockProvider implements TemporaryMenuListener {
		@Getter
		private final Player player;
		private final ShopProvider previousMenu;
		private final Product product;

		public AddStockProvider(Player player, ShopProvider previousMenu, Product product) {
			this.player = player;
			this.previousMenu = previousMenu;
			this.product = product;

			product.setEditing(true);
			open();
		}

		@Override
		public void onClose(InventoryCloseEvent event, List<ItemStack> contents) {
			for (ItemStack content : contents) {
				if (Nullables.isNullOrAir(content))
					continue;

				if (ItemUtils.isSimilar(product.getItem(), content))
					product.addStock(content.getAmount());
				else
					PlayerUtils.giveItem(player, content);
			}

			product.setEditing(false);
			new ShopService().save(product.getShop());

			if (previousMenu != null)
				Tasks.wait(1, () -> previousMenu.open(player));
		}

	}

	@Title("&0Mass Add Stock")
	public static class MassAddStockProvider implements TemporaryMenuListener {
		@Getter
		private final Player player;
		private final ShopProvider previousMenu;
		private final Shop shop;

		public MassAddStockProvider(Player player, ShopProvider previousMenu, Shop shop) {
			this.player = player;
			this.previousMenu = previousMenu;
			this.shop = shop;

			shop.getProducts().forEach(product -> product.setEditing(true));
			open();
		}

		@Override
		public void onClose(InventoryCloseEvent event, List<ItemStack> contents) {
			contentsLoop: for (ItemStack content : contents) {
				if (Nullables.isNullOrAir(content))
					continue;

				for (var product : shop.getProducts()) {
					if (ItemUtils.isSimilar(product.getItem(), content)) {
						product.addStock(content.getAmount());
						continue contentsLoop;
					}
				}

				PlayerUtils.giveItem(player, content);
			}

			shop.getProducts().forEach(product -> product.setEditing(false));
			new ShopService().save(shop);

			if (previousMenu != null)
				Tasks.wait(1, () -> previousMenu.open(player));
		}

	}

	@Title("&0Remove Stock")
	public static class RemoveStockProvider implements TemporaryMenuListener {
		@Getter
		private final Player player;
		private final ShopProvider previousMenu;
		private final Product product;
		private int itemsAdded;

		public RemoveStockProvider(Player player, ShopProvider previousMenu, Product product) {
			this.player = player;
			this.previousMenu = previousMenu;
			this.product = product;

			product.setEditing(true);

			List<ItemStack> items = product.getItemStacks(54);

			for (ItemStack item : items)
				itemsAdded += item.getAmount();

			open(items);
		}

		@Override
		public void onClose(InventoryCloseEvent event, List<ItemStack> contents) {
			int itemsLeft = 0;
			for (ItemStack content : contents) {
				if (Nullables.isNullOrAir(content))
					continue;

				if (!ItemUtils.isSimilar(product.getItem(), content)) {
					PlayerUtils.giveItem(player, content);
					continue;
				}

				itemsLeft += content.getAmount();
			}

			product.removeStock(itemsAdded - itemsLeft);
			new ShopService().save(product.getShop());
			product.setEditing(false);

			if (previousMenu != null)
				Tasks.wait(1, () -> previousMenu.open(player));
		}

	}

}
