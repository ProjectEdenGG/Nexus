package gg.projecteden.nexus.features.shops.providers;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.shops.providers.common.ShopProvider;
import gg.projecteden.nexus.models.shop.Shop;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MainMenuProvider extends ShopProvider {

	public MainMenuProvider(ShopProvider previousMenu) {
		this.previousMenu = previousMenu;
	}

	@Override
	public void open(Player player, int page) {
		open(player, page, this, "&0Shops");
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		super.init(player, contents);

		contents.set(1, 2, ClickableItem.of(nameItem(Material.OAK_SIGN, "&6&lBrowse Market"), e -> new BrowseMarketProvider(this).open(player)));
		contents.set(1, 4, ClickableItem.of(nameItem(Material.CHEST, "&6&lBrowse Shops"), e -> new BrowseShopsProvider(this).open(player)));
		contents.set(1, 6, ClickableItem.of(nameItem(Material.CHEST, "&6&lBrowse Items"), e -> new BrowseProductsProvider(this).open(player)));

		contents.set(3, 3, ClickableItem.of(nameItem(Material.COMPASS, "&6&lSearch Items"), e -> new SearchProductsProvider(this).open(player)));
		ItemStack head = new ItemBuilder(Material.PLAYER_HEAD).skullOwner(player).name("&6&lYour Shop").build();
		contents.set(3, 5, ClickableItem.of(head, e -> new YourShopProvider(this).open(player)));

		updateTask(player, contents);
	}

	private void updateTask(Player player, InventoryContents contents) {
		final AtomicInteger index = new AtomicInteger();
		final AtomicInteger taskId = new AtomicInteger();

		final List<Shop> shops = service.getShopsSorted(shopGroup);

		taskId.set(Tasks.repeat(0, 30, () -> {
			if (!isOpen(player)) {
				Tasks.cancel(taskId.get());
				return;
			}

			if (shops.isEmpty())
				return;

			if (index.get() >= shops.size())
				index.set(0);

			ItemBuilder owner = new ItemBuilder(Material.PLAYER_HEAD)
					.skullOwner(shops.get(index.getAndIncrement()))
					.name("&6&lBrowse Shops");

			contents.set(1, 4, ClickableItem.of(owner.build(), e -> new BrowseShopsProvider(this).open(player)));
		}));
	}

}
