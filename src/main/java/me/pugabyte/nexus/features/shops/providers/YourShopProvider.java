package me.pugabyte.nexus.features.shops.providers;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import lombok.Getter;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.listeners.TemporaryListener;
import me.pugabyte.nexus.features.shops.Shops;
import me.pugabyte.nexus.features.shops.providers.common.ShopProvider;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.models.shop.Shop;
import me.pugabyte.nexus.models.shop.ShopService;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Utils;
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

import static me.pugabyte.nexus.utils.StringUtils.colorize;

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

		contents.set(0, 1, ClickableItem.from(nameItem(Material.ENDER_EYE, "&6Preview your shop"), e -> new PlayerShopProvider(this, shop).open(player)));

		ItemBuilder description = new ItemBuilder(Material.OAK_SIGN).name("&6Set shop description");
		if (!shop.getDescription().isEmpty())
			description.lore("").lore(shop.getDescription());

		contents.set(0, 2, ClickableItem.from(description.build(), e -> Nexus.getSignMenuFactory()
				.lines(shop.getDescriptionArray())
				.prefix(Shops.PREFIX)
				.colorize(false)
				.response(lines -> {
					shop.setDescription(Arrays.asList(lines));
					service.save(shop);
					open(player);
				}).open(player)));

		contents.set(0, 4, ClickableItem.from(nameItem(Material.LIME_CONCRETE_POWDER, "&6Add item"), e -> new ExchangeConfigProvider(this).open(player)));

		contents.set(0, 6, ClickableItem.from(nameItem(Material.WRITABLE_BOOK, "&6Shop history"), e -> {
			PlayerUtils.runCommand(player, "shop history");
			player.closeInventory();
		}));
		contents.set(0, 7, ClickableItem.from(nameItem(Material.CYAN_SHULKER_BOX, "&6Collect items"), e -> new CollectItemsProvider(this).open(player)));

		contents.set(5, 3, ClickableItem.from(nameItem(Material.RED_CONCRETE_POWDER, "&cDisable all", "&f||&7Click to disable all items"), e -> {
			ConfirmationMenu.builder()
					.onConfirm(e2 -> {
						shop.getProducts().forEach(product -> product.setEnabled(false));
						service.save(shop);
						open(player, page);
					})
					.onCancel(e2 -> open(player, page))
					.open(player);
		}));
		contents.set(5, 5, ClickableItem.from(nameItem(Material.LIME_CONCRETE_POWDER, "&aEnable all", "&f||&7Click to enable all items"), e -> {
			ConfirmationMenu.builder()
					.onConfirm(e2 -> {
						shop.getProducts().forEach(product -> product.setEnabled(true));
						service.save(shop);
						open(player, page);
					})
					.onCancel(e2 -> open(player, page))
					.open(player);
		}));

		if (shop.getProducts() == null || shop.getProducts().size() == 0) return;
		List<ClickableItem> items = new ArrayList<>();

		shop.getProducts(shopGroup).forEach(product -> {
			ItemStack item = product.getItemWithOwnLore().build();
			items.add(ClickableItem.from(item, e -> {
				if (handleRightClick(product, e))
					return;
				new EditProductProvider(this, product).open(player);
			}));
		});

		addPagination(player, contents, items);
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
				if (!ItemUtils.isNullOrAir(content))
					shop.addHolding(content);

			service.save(shop);

			Nexus.unregisterTemporaryListener(this);
			event.getPlayer().closeInventory();
			if (previousMenu != null)
				Tasks.wait(1, () -> previousMenu.open(player));
		}
	}

}
