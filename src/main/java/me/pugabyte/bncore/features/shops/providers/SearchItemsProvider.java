package me.pugabyte.bncore.features.shops.providers;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.shops.ShopMenuFunctions.FilterSearchType;
import me.pugabyte.bncore.utils.MaterialTag;
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

		contents.set(1, 1, ClickableItem.from(nameItem(Material.NAME_TAG, "&6Search by item name"),
				e -> BNCore.getSignMenuFactory().lines("", "^ ^ ^ ^ ^ ^", "Enter a", "search term").response(lines -> {
					try {
						if (lines[0].length() > 0) {
							((BrowseItemsProvider) previousMenu).getFilters().add(FilterSearchType.SEARCH.of(lines[0], product ->
									product.getItem().getType().name().toLowerCase().contains(lines[0].toLowerCase())));
							previousMenu.open(player);
						} else
							open(player);
					} catch (Throwable ex) {
						player.sendMessage(ex.getMessage());
						open(player);
					}
				})
						.open(player)));

		contents.set(1, 3, ClickableItem.from(nameItem(Material.APPLE, "&6Search for food"), e -> {
			((BrowseItemsProvider) previousMenu).getFilters().add(FilterSearchType.SEARCH
					.of("Food", product -> product.getItem().getType().isEdible()));
			previousMenu.open(player);
		}));

		contents.set(1, 5, ClickableItem.from(nameItem(Material.ENCHANTED_BOOK, "&6Search for enchanted items"), e -> {
			((BrowseItemsProvider) previousMenu).getFilters().add(FilterSearchType.SEARCH.of("Enchanted items", product -> {
				if (product.getItem().getType().equals(Material.ENCHANTED_BOOK)) {
					EnchantmentStorageMeta book = (EnchantmentStorageMeta) product.getItem().getItemMeta();
					return book != null && !book.getStoredEnchants().isEmpty();
				} else {
					return !product.getItem().getEnchantments().isEmpty();
				}
			}));
			previousMenu.open(player);
		}));

		contents.set(1, 7, ClickableItem.from(nameItem(Material.DIAMOND_SWORD, "&6Search for tools,", "&6weapons and armor"), e -> {
			((BrowseItemsProvider) previousMenu).getFilters().add(FilterSearchType.SEARCH.of("Tools, weapons and armor", product ->
					MaterialTag.TOOLS_WEAPONS_ARMOR.isTagged(product.getItem().getType())));
			previousMenu.open(player);
		}));
	}

}
