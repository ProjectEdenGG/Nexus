package gg.projecteden.nexus.features.shops.providers;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import gg.projecteden.nexus.features.shops.providers.common.ShopProvider;
import gg.projecteden.nexus.models.nerd.NerdService;
import gg.projecteden.nexus.models.shop.Shop;
import gg.projecteden.nexus.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static gg.projecteden.nexus.utils.StringUtils.plural;

public class BrowseShopsProvider extends ShopProvider {

	public BrowseShopsProvider(ShopProvider previousMenu) {
		this.previousMenu = previousMenu;
	}

	@Override
	public void open(Player player, int page) {
		open(player, page, this, "&0Browse Shops");
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		super.init(player, contents);
		addItems(player, contents);
	}

	public void addItems(Player player, InventoryContents contents) {
		List<Shop> shops = service.getShopsSorted(shopGroup);
		if (shops.isEmpty())
			return;

		List<ClickableItem> items = new ArrayList<>();

		for (Shop shop : shops) {
			int inStock = shop.getInStock(shopGroup).size();
			int outOfStock = shop.getOutOfStock(shopGroup).size();
			ItemBuilder head = new ItemBuilder(Material.PLAYER_HEAD)
					.skullOwner(shop.getOfflinePlayer())
					.name(new NerdService().get(shop.getOfflinePlayer()).getColoredName())
					.lore((inStock == 0 ? "&c" : "&a") + inStock + " " + plural("product", inStock) + " in stock")
					.lore((outOfStock == 0 ? "&a" : "&c") + outOfStock + " " + plural("product", outOfStock) + " out of stock");

			List<String> description = shop.getDescription();
			if (!description.isEmpty())
				head.lore("&f").lore(description);

			items.add(ClickableItem.from(head.build(), e -> new PlayerShopProvider(this, shop).open(player)));
		}

		addPagination(player, contents, items);
	}

}
