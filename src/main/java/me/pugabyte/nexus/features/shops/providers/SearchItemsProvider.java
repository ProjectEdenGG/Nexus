package me.pugabyte.nexus.features.shops.providers;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.shops.ShopMenuFunctions.FilterSearchType;
import me.pugabyte.nexus.features.shops.Shops;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.PlayerUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

public class SearchItemsProvider extends _ShopProvider {

	public SearchItemsProvider(_ShopProvider previousMenu) {
		this.previousMenu = previousMenu;
	}

	@Override
	public void open(Player viewer, int page) {
		open(viewer, page, this, "&0Search Items");
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		super.init(player, contents);

		BrowseItemsProvider browseItemsMenu;
		if (previousMenu instanceof BrowseItemsProvider)
			browseItemsMenu = (BrowseItemsProvider) previousMenu;
		else
			browseItemsMenu = new BrowseItemsProvider(this);

		contents.set(1, 1, ClickableItem.from(nameItem(Material.NAME_TAG, "&6Search by item name"),
				e -> Nexus.getSignMenuFactory().lines("", "^ ^ ^ ^ ^ ^", "Enter a", "search term").prefix(Shops.PREFIX).response(lines -> {
					try {
						if (lines[0].length() > 0) {
							browseItemsMenu.getFilters().add(FilterSearchType.SEARCH.of(lines[0], product ->
									product.getItem().getType().name().toLowerCase().contains(lines[0].toLowerCase())));
							browseItemsMenu.open(player);
						} else
							open(player);
					} catch (Exception ex) {
						PlayerUtils.send(player, ex.getMessage());
						open(player);
					}
				}).open(player)));

		contents.set(1, 3, ClickableItem.from(nameItem(Material.APPLE, "&6Search for food"), e -> {
			browseItemsMenu.getFilters().add(FilterSearchType.SEARCH
					.of("Food", product -> product.getItem().getType().isEdible()));
			browseItemsMenu.open(player);
		}));

		contents.set(1, 5, ClickableItem.from(nameItem(Material.ENCHANTED_BOOK, "&6Search for enchanted items"), e -> {
			browseItemsMenu.getFilters().add(FilterSearchType.SEARCH.of("Enchanted items", product -> {
				if (product.getItem().getType().equals(Material.ENCHANTED_BOOK)) {
					EnchantmentStorageMeta book = (EnchantmentStorageMeta) product.getItem().getItemMeta();
					return book != null && !book.getStoredEnchants().isEmpty();
				} else {
					return !product.getItem().getEnchantments().isEmpty();
				}
			}));
			browseItemsMenu.open(player);
		}));

		contents.set(1, 7, ClickableItem.from(nameItem(Material.DIAMOND_SWORD, "&6Search for tools,", "&6weapons and armor"), e -> {
			browseItemsMenu.getFilters().add(FilterSearchType.SEARCH.of("Tools, weapons and armor", product ->
					MaterialTag.TOOLS_WEAPONS_ARMOR.isTagged(product.getItem().getType())));
			browseItemsMenu.open(player);
		}));
	}

}
