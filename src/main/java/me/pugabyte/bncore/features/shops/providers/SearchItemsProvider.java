package me.pugabyte.bncore.features.shops.providers;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.shops.ShopMenuFunctions.FilterSearchType;
import me.pugabyte.bncore.utils.MaterialTag;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.Collections;

import static me.pugabyte.bncore.utils.StringUtils.colorize;

public class SearchItemsProvider extends _ShopProvider {

	public SearchItemsProvider(_ShopProvider previousMenu) {
		this.previousMenu = previousMenu;
	}

	@Override
	public void open(Player viewer, int page) {
		SmartInventory.builder()
				.provider(this)
				.title(colorize("&0Search Items"))
				.size(6, 9)
				.build()
				.open(viewer, page);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		super.init(player, contents);

		contents.set(1, 1, ClickableItem.from(nameItem(Material.NAME_TAG, "&6Search by item name"),
				e -> BNCore.getSignMenuFactory().lines("", "^ ^ ^ ^ ^ ^", "Enter a", "search term").response((_player, response) -> {
					try {
						if (response[0].length() > 0)
							new BrowseItemsProvider(this, Collections.singletonList(FilterSearchType.SEARCH
									.of(response[0], product -> product.getItem().getType().name().toLowerCase().contains(response[0].toLowerCase()))))
									.open(player);
						else
							open(player);
					} catch (Exception ex) {
						_player.sendMessage(ex.getMessage());
						open(player);
					}
				})
				.open(player)));

		contents.set(1, 3, ClickableItem.from(nameItem(Material.APPLE, "&6Search for food"),
				e -> new BrowseItemsProvider(this, Collections.singletonList(FilterSearchType.SEARCH
						.of("Food", product -> product.getItem().getType().isEdible())))
						.open(player)));

		contents.set(1, 5, ClickableItem.from(nameItem(Material.ENCHANTED_BOOK, "&6Search for enchanted items"),
				e -> new BrowseItemsProvider(this, Collections.singletonList(FilterSearchType.SEARCH.of("Enchanted items", product -> {
					if (product.getItem().getType().equals(Material.ENCHANTED_BOOK)) {
						EnchantmentStorageMeta book = (EnchantmentStorageMeta) product.getItem().getItemMeta();
						return book != null && !book.getStoredEnchants().isEmpty();
					} else {
						return !product.getItem().getEnchantments().isEmpty();
					}
				}))).open(player)));

		contents.set(1, 7, ClickableItem.from(nameItem(Material.DIAMOND_SWORD, "&6Search for tools,", "&6weapons and armor"),
				e -> new BrowseItemsProvider(this, Collections.singletonList(FilterSearchType.SEARCH
						.of("Tools, weapons and armor", product -> MaterialTag.TOOLS_WEAPONS_ARMOR.isTagged(product.getItem().getType()))))
						.open(player)));
	}

}
