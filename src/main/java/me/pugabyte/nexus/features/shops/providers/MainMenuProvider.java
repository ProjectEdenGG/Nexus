package me.pugabyte.nexus.features.shops.providers;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.SmartInvsPlugin;
import fr.minuskube.inv.content.InventoryContents;
import me.pugabyte.nexus.models.shop.Shop;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Time;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class MainMenuProvider extends _ShopProvider {

	public MainMenuProvider(_ShopProvider previousMenu) {
		this.previousMenu = previousMenu;
	}

	@Override
	public void open(Player viewer, int page) {
		open(viewer, page, this, "&0Shops");
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		super.init(player, contents);

		contents.set(1, 2, ClickableItem.from(nameItem(Material.OAK_SIGN, "&6&lBrowse Market"), e -> new BrowseMarketProvider(this).open(player)));
		contents.set(1, 4, ClickableItem.from(nameItem(Material.CHEST, "&6&lBrowse Shops"), e -> new BrowseShopsProvider(this).open(player)));
		contents.set(1, 6, ClickableItem.from(nameItem(Material.CHEST, "&6&lBrowse Items"), e -> new BrowseItemsProvider(this).open(player)));

		contents.set(3, 3, ClickableItem.from(nameItem(Material.COMPASS, "&6&lSearch Items"), e -> new SearchItemsProvider(this).open(player)));
		ItemStack head = new ItemBuilder(Material.PLAYER_HEAD).skullOwner(player).name("&6&lYour Shop").build();
		contents.set(3, 5, ClickableItem.from(head, e -> new YourShopProvider(this).open(player)));

		updateTask(player, contents);
	}

	private void updateTask(Player player, InventoryContents contents) {
		final AtomicInteger index = new AtomicInteger();
		final AtomicInteger taskId = new AtomicInteger();

		final List<Shop> shops = service.getShops().stream()
				.filter(shop -> !shop.isMarket() && !shop.getProducts(shopGroup).isEmpty())
				.sorted(Comparator.comparing(shop -> shop.getInStock(shopGroup).size(), Comparator.reverseOrder()))
				.collect(Collectors.toList());

		taskId.set(Tasks.repeat(0, Time.SECOND.x(2), () -> {
			Optional<SmartInventory> inventory = SmartInvsPlugin.manager().getInventory(player);
			if (!(inventory.isPresent() && this.equals(inventory.get().getProvider()))) {
				Tasks.cancel(taskId.get());
				return;
			}

			if (shops.isEmpty())
				return;

			if (index.get() >= shops.size())
				index.set(0);

			ItemBuilder owner = new ItemBuilder(Material.PLAYER_HEAD)
					.skullOwner(shops.get(index.getAndIncrement()).getOfflinePlayer())
					.name("&6&lBrowse Shops");

			contents.set(1, 4, ClickableItem.from(owner.build(), e -> new BrowseShopsProvider(this).open(player)));
		}));
	}

}
