package me.pugabyte.bncore.features.shops.providers;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.Pagination;
import me.pugabyte.bncore.models.shop.Shop;
import me.pugabyte.bncore.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static me.pugabyte.bncore.utils.StringUtils.colorize;

public class BrowseShopsProvider extends _ShopProvider {

	public BrowseShopsProvider(_ShopProvider previousMenu) {
		this.previousMenu = previousMenu;
	}

	@Override
	public void open(Player viewer, int page) {
		SmartInventory.builder()
				.provider(this)
				.title(colorize("&0Browse Shops"))
				.size(6, 9)
				.build()
				.open(viewer, page);
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
			ItemStack head = new ItemBuilder(Material.PLAYER_HEAD).skullOwner(shop.getOfflinePlayer()).build();
			items.add(ClickableItem.from(nameItem(head, "&e" + shop.getOfflinePlayer().getName()), e -> new PlayerShopProvider(shop, this).open(player)));
		});

		addPagination(player, contents, items);
	}

}
