package gg.projecteden.nexus.features.shops.providers;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.listeners.TemporaryListener;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.shops.Shops;
import gg.projecteden.nexus.features.shops.providers.common.ShopProvider;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.shop.Shop;
import gg.projecteden.nexus.models.shop.ShopService;
import gg.projecteden.nexus.utils.ItemBuilder;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;
import static gg.projecteden.nexus.utils.StringUtils.colorize;

public class YourShopProvider extends ShopProvider {

	public YourShopProvider(ShopProvider previousMenu) {
		this.previousMenu = previousMenu;
	}

	@Override
	public void open(Player player, int page) {
		open(player, page, this, "&0Your shop");
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		super.init(player, contents);

		Shop shop = new ShopService().get(player);

		contents.set(0, 1, ClickableItem.of(Material.ENDER_EYE, "&6Preview your shop", e -> new PlayerShopProvider(this, shop).open(player)));

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
					open(player);
				}).open(player)));

		contents.set(0, 4, ClickableItem.of(Material.LIME_CONCRETE_POWDER, "&6Add item", e -> new ExchangeConfigProvider(this).open(player)));

		contents.set(0, 6, ClickableItem.of(Material.WRITABLE_BOOK, "&6Shop history", e -> {
			PlayerUtils.runCommand(player, "shop history");
			player.closeInventory();
		}));
		contents.set(0, 7, ClickableItem.of(Material.CYAN_SHULKER_BOX, "&6Collect items", e -> new CollectItemsProvider(this).open(player)));

		contents.set(5, 3, ClickableItem.of(new ItemBuilder(Material.RED_CONCRETE_POWDER).name("&cDisable all").lore("", "&7Click to disable all items"), e3 ->
			ConfirmationMenu.builder()
				.onConfirm(e21 -> {
					shop.getProducts().forEach(product2 -> product2.setEnabled(false));
					service.save(shop);
					open(player, page);
				})
				.onCancel(e21 -> open(player, page))
				.open(player)));
		contents.set(5, 5, ClickableItem.of(new ItemBuilder(Material.LIME_CONCRETE_POWDER).name("&aEnable all").lore("", "&7Click to enable all items"), e1 ->
			ConfirmationMenu.builder()
				.onConfirm(e2 -> {
					shop.getProducts().forEach(product1 -> product1.setEnabled(true));
					service.save(shop);
					open(player, page);
				})
				.onCancel(e2 -> open(player, page))
				.open(player)));

		if (shop.getProducts() == null || shop.getProducts().size() == 0) return;
		List<ClickableItem> items = new ArrayList<>();

		shop.getProducts(shopGroup).forEach(product -> {
			ItemStack item = product.getItemWithOwnLore().build();
			items.add(ClickableItem.of(item, e -> {
				if (handleRightClick(product, e))
					return;
				new EditProductProvider(this, product).open(player);
			}));
		});

		paginator(player, contents, items);
	}

	public static class CollectItemsProvider extends ShopProvider implements TemporaryListener {
		private final static String TITLE = colorize("&0Collect Items");
		@Getter
		private Player player;
		private final ShopProvider previousMenu;

		public CollectItemsProvider(ShopProvider previousMenu) {
			this.previousMenu = previousMenu;
		}

		public void open(Player player, int page) {
			this.player = player;

			final int size = 54;
			Inventory inv = Bukkit.createInventory(null, size, TITLE);

			ShopService service = new ShopService();
			Shop shop = service.get(player);

			if (shop.getHolding().isEmpty())
				throw new InvalidInputException("No items available for collection");

			List<ItemStack> items = new ArrayList<>();
			final int max = Math.min(size, shop.getHolding().size());
			final Iterator<ItemStack> iterator = shop.getHolding().iterator();
			while (items.size() < max && iterator.hasNext()) {
				items.add(iterator.next());
				iterator.remove();
			}
			service.save(shop);

			inv.setContents(items.toArray(ItemStack[]::new));
			Nexus.registerTemporaryListener(this);
			player.openInventory(inv);
		}

		@EventHandler
		public void onChestClose(InventoryCloseEvent event) {
			if (event.getInventory().getHolder() != null) return;
			if (!Utils.equalsInvViewTitle(event.getView(), TITLE)) return;
			if (!event.getPlayer().equals(player)) return;

			ShopService service = new ShopService();
			Shop shop = service.get(player);

			for (ItemStack content : event.getInventory().getContents())
				if (!isNullOrAir(content))
					shop.addHolding(content);

			service.save(shop);

			Nexus.unregisterTemporaryListener(this);
			event.getPlayer().closeInventory();
			if (previousMenu != null)
				Tasks.wait(1, () -> previousMenu.open(player));
		}
	}

}
