package gg.projecteden.nexus.features.shops.providers;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.listeners.TemporaryListener;
import gg.projecteden.nexus.features.shops.ShopCommand;
import gg.projecteden.nexus.features.shops.Shops;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.shop.Shop;
import gg.projecteden.nexus.models.shop.Shop.ExchangeType;
import gg.projecteden.nexus.models.shop.Shop.Product;
import gg.projecteden.nexus.models.shop.ShopService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static gg.projecteden.nexus.features.menus.SignMenuFactory.ARROWS;
import static gg.projecteden.nexus.utils.ItemUtils.isSimilar;
import static gg.projecteden.nexus.utils.StringUtils.colorize;
import static gg.projecteden.nexus.utils.StringUtils.pretty;

public class EditProductProvider extends _ShopProvider {
	private final Product product;

	public EditProductProvider(_ShopProvider previousMenu, Product product) {
		this.previousMenu = previousMenu;
		this.product = product;
		this.rows = 4;
	}

	@Override
	public void open(Player player, int page) {
		open(player, page, this, "&0Edit Item");
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		super.init(player, contents);

		contents.set(0, 4, ClickableItem.from(product.getItemWithOwnLore().build(), e -> new ExchangeConfigProvider(this, product).open(player)));
		if (product.getExchangeType() == ExchangeType.BUY) {
			ItemBuilder builder = new ItemBuilder(Material.GOLD_INGOT)
					.name("&6Edit Stock")
					.lore("&7Enter the dollar amount you are")
					.lore("&7willing to spend on this item, or")
					.lore("&7enter -1 to allow unlimited purchases")
					.loreize(false);

			contents.set(1, 4, ClickableItem.from(builder.build(), e -> Nexus.getSignMenuFactory()
					.lines("", ARROWS, "Enter an amount", "or -1 for no limit")
					.prefix(Shops.PREFIX)
					.onError(() -> open(player))
					.response(lines -> {
						if (lines[0].length() > 0) {
							String input = lines[0].replaceAll("[^0-9.-]+", "");
							if (!Utils.isDouble(input))
								throw new InvalidInputException("Could not parse &e" + lines[0] + " &cas a dollar amount");
							double stock = new BigDecimal(input).setScale(2, RoundingMode.HALF_UP).doubleValue();
							if (!(stock == -1 || stock >= 0))
								throw new InvalidInputException("Stock must be -1 (unlimited), or $0 or greater");
							product.setStock(stock);
							service.save(product.getShop());
						}
						open(player);
					}).open(player)
			));
		} else {
			contents.set(1, 3, ClickableItem.from(new ItemBuilder(Material.LIME_CONCRETE_POWDER).name("&6Add Stock").lore("&f", "&7Right click to add in bulk").build(), e -> {
				if (isRightClick(e)) {
					player.closeInventory();
					ShopCommand.getInteractStockMap().put(player.getUniqueId(), product);
					PlayerUtils.send(player, new JsonBuilder(Shops.PREFIX + "Right click any container (ie chest, shulker box, etc) to stock &e"
							+ pretty(product.getItem()) + "&3. &eClick here to end").command("/shop cancelInteractStock"));
				} else {
					new AddStockProvider(this, product).open(player);
				}
			}));
			contents.set(1, 5, ClickableItem.from(nameItem(Material.RED_CONCRETE_POWDER, "&6Remove Stock"), e -> new RemoveStockProvider(this, product).open(player)));
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

		contents.set(3, 2, ClickableItem.from(purchasable.build(), e -> {
			product.setPurchasable(!product.isPurchasable());
			service.save(product.getShop());
			open(player);
		}));

		contents.set(3, 4, ClickableItem.from(enabled.build(), e -> {
			product.setEnabled(!product.isEnabled());
			service.save(product.getShop());
			open(player);
		}));

		contents.set(3, 6, ClickableItem.from(new ItemBuilder(Material.LAVA_BUCKET).name("&cDelete").build(), e ->
				ConfirmationMenu.builder()
						.onConfirm(e2 -> {
							Shop shop = service.get(player);
							shop.removeProduct(product);
							service.save(shop);
							previousMenu.open(player);
						})
						.onCancel(e2 -> open(player))
						.open(player)));

	}

	public static class AddStockProvider extends _ShopProvider implements TemporaryListener {
		private final static String TITLE = colorize("&0Add Stock");
		@Getter
		private Player player;
		private final _ShopProvider previousMenu;
		private final Product product;

		public AddStockProvider(_ShopProvider previousMenu, Product product) {
			this.previousMenu = previousMenu;
			this.product = product;
		}

		public void open(Player player, int page) {
			this.player = player;
			product.setEditing(true);

			Inventory inv = Bukkit.createInventory(null, 54, TITLE);
			Nexus.registerTemporaryListener(this);
			player.openInventory(inv);
		}

		@EventHandler
		public void onChestClose(InventoryCloseEvent event) {
			if (event.getInventory().getHolder() != null) return;
			if (!Utils.equalsInvViewTitle(event.getView(), TITLE)) return;
			if (!event.getPlayer().equals(player)) return;

			ItemStack[] contents = event.getInventory().getContents();
			for (ItemStack content : contents) {
				if (ItemUtils.isNullOrAir(content))
					continue;

				if (isSimilar(product.getItem(), content))
					product.addStock(content.getAmount());
				else
					PlayerUtils.giveItem(player, content);
			}

			new ShopService().save(product.getShop());
			product.setEditing(false);

			Nexus.unregisterTemporaryListener(this);
			event.getPlayer().closeInventory();
			Tasks.wait(1, () -> previousMenu.open(player));
		}
	}

	public static class RemoveStockProvider extends _ShopProvider implements TemporaryListener {
		private final static String TITLE = colorize("&0Remove Stock");
		@Getter
		private Player player;
		private final _ShopProvider previousMenu;
		private final Product product;
		private int itemsAdded;

		public RemoveStockProvider(_ShopProvider previousMenu, Product product) {
			this.previousMenu = previousMenu;
			this.product = product;
		}

		public void open(Player player, int page) {
			this.player = player;
			product.setEditing(true);

			final int size = 54;
			Inventory inv = Bukkit.createInventory(null, size, TITLE);

			List<ItemStack> items = product.getItemStacks(size);

			for (ItemStack item : items)
				itemsAdded += item.getAmount();

			inv.setContents(items.toArray(ItemStack[]::new));
			Nexus.registerTemporaryListener(this);
			player.openInventory(inv);
		}

		@EventHandler
		public void onChestClose(InventoryCloseEvent event) {
			if (event.getInventory().getHolder() != null) return;
			if (!Utils.equalsInvViewTitle(event.getView(), TITLE)) return;
			if (!event.getPlayer().equals(player)) return;

			ItemStack[] contents = event.getInventory().getContents();
			int itemsLeft = 0;
			for (ItemStack content : contents) {
				if (ItemUtils.isNullOrAir(content))
					continue;

				if (!isSimilar(product.getItem(), content)) {
					PlayerUtils.giveItem(player, content);
					continue;
				}

				itemsLeft += content.getAmount();
			}

			product.removeStock(itemsAdded - itemsLeft);
			new ShopService().save(product.getShop());
			product.setEditing(false);

			Nexus.unregisterTemporaryListener(this);
			event.getPlayer().closeInventory();
			Tasks.wait(1, () -> previousMenu.open(player));
		}
	}

}
