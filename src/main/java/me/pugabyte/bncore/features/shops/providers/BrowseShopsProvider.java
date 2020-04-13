package me.pugabyte.bncore.features.shops.providers;

import com.google.common.base.Strings;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.Pagination;
import me.pugabyte.bncore.models.shop.Shop;
import me.pugabyte.bncore.utils.ItemBuilder;
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

		Pagination page = contents.pagination();

		service.getShops().forEach(shop -> {
			ItemBuilder head = new ItemBuilder(Material.PLAYER_HEAD)
					.skullOwner(shop.getOfflinePlayer())
					.name("&e" + shop.getOfflinePlayer().getName());
			shop.getDescription().stream().filter(line -> !Strings.isNullOrEmpty(line)).forEach(head::lore);

			items.add(ClickableItem.from(head.build(), e -> new PlayerShopProvider(this, shop).open(player)));
		});

		addPagination(player, contents, items);
	}

}
