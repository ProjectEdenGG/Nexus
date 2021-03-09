package me.pugabyte.nexus.features.shops.providers;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.models.shop.Shop;
import me.pugabyte.nexus.models.shop.Shop.ShopGroup;
import me.pugabyte.nexus.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BrowseShopsProvider extends _ShopProvider {

	public BrowseShopsProvider(_ShopProvider previousMenu) {
		this.previousMenu = previousMenu;
	}

	@Override
	public void open(Player viewer, int page) {
		open(viewer, page, this, "&0Browse Shops");
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		super.init(player, contents);
		addItems(player, contents);
	}

	public void addItems(Player player, InventoryContents contents) {
		List<Shop> shops = service.getShops();
		if (shops == null || shops.size() == 0) return;
		List<ClickableItem> items = new ArrayList<>();

		service.getShops().forEach(shop -> {
			if (shop.getUuid().equals(Nexus.getUUID0())) return;
			if (shop.getProducts(ShopGroup.get(player)).isEmpty()) return;

			ItemBuilder head = new ItemBuilder(Material.PLAYER_HEAD)
					.skullOwner(shop.getOfflinePlayer())
					.name("&e" + shop.getOfflinePlayer().getName())
					.lore(shop.getDescription());

			items.add(ClickableItem.from(head.build(), e -> new PlayerShopProvider(this, shop).open(player)));
		});

		addPagination(player, contents, items);
	}

}
