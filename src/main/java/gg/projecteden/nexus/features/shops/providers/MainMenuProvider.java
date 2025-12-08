package gg.projecteden.nexus.features.shops.providers;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.shops.providers.common.ShopProvider;
import gg.projecteden.nexus.models.shop.Shop;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Title("&0Shops")
public class MainMenuProvider extends ShopProvider {

	public MainMenuProvider(ShopProvider previousMenu) {
		this.previousMenu = previousMenu;
	}

	@Override
	public void init() {
		super.init();

//		contents.set(1, 2, ClickableItem.of(Material.OAK_SIGN, "&6&lBrowse Market", e -> new BrowseMarketProvider(this).open(player)));
		contents.set(1, 3, ClickableItem.of(Material.CHEST, "&6&lBrowse Shops", e -> new BrowseShopsProvider(this).open(viewer)));
		contents.set(1, 5, ClickableItem.of(Material.CHEST, "&6&lBrowse Items", e -> new BrowseProductsProvider(this).open(viewer)));

		contents.set(3, 3, ClickableItem.of(Material.COMPASS, "&6&lSearch Items", e -> new SearchProductsProvider(this).open(viewer)));
		ItemStack head = new ItemBuilder(Material.PLAYER_HEAD).skullOwner(viewer).name("&6&lYour Shop").build();
		contents.set(3, 5, ClickableItem.of(head, e -> new YourShopProvider(service.get(viewer), this).open(viewer)));

		updateTask();
	}

	private void updateTask() {
		final AtomicInteger index = new AtomicInteger();
		final AtomicInteger taskId = new AtomicInteger();

		final List<Shop> shops = service.getShopsSorted(shopGroup);

		taskId.set(Tasks.repeat(0, 30, () -> {
			if (!isOpen()) {
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

			contents.set(1, 3, ClickableItem.of(owner.build(), e -> new BrowseShopsProvider(this).open(viewer)));
		}));
	}

}
