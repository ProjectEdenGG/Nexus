package gg.projecteden.nexus.features.shops.providers;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.shops.Shops;
import gg.projecteden.nexus.features.shops.providers.common.ShopMenuFunctions.FilterSearchType;
import gg.projecteden.nexus.features.shops.providers.common.ShopProvider;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.function.Predicate;

import static gg.projecteden.nexus.features.menus.SignMenuFactory.ARROWS;
import static gg.projecteden.nexus.utils.StringUtils.stripColor;

public class SearchProductsProvider extends ShopProvider {

	public SearchProductsProvider(ShopProvider previousMenu) {
		this.previousMenu = previousMenu;
	}

	@Override
	public void open(Player player, int page) {
		open(player, page, this, "&0Search Items");
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		super.init(player, contents);

		BrowseProductsProvider browseItemsMenu;
		if (previousMenu instanceof BrowseProductsProvider)
			browseItemsMenu = (BrowseProductsProvider) previousMenu;
		else
			browseItemsMenu = new BrowseProductsProvider(this);

		contents.set(1, 1, ClickableItem.from(nameItem(Material.NAME_TAG, "&6Search by item name"), e -> Nexus.getSignMenuFactory()
				.lines("", ARROWS, "Enter a", "search term")
				.prefix(Shops.PREFIX)
				.onError(() -> open(player))
				.response(lines -> {
					String input = stripColor(lines[0]);
					if (input.length() > 0) {
						browseItemsMenu.getFilters().add(FilterSearchType.SEARCH.of(input));
						browseItemsMenu.open(player);
					} else
						open(player);
				}).open(player)));

		contents.set(1, 3, ClickableItem.from(nameItem(Material.APPLE, "&6Search for food"), e -> {
			browseItemsMenu.getFilters().add(FilterSearchType.SEARCH.of("Food", product -> filter(product.getItem(), MaterialTag.FOODS)));
			browseItemsMenu.open(player);
		}));

		contents.set(1, 5, ClickableItem.from(nameItem(Material.ENCHANTED_BOOK, "&6Search for enchanted items"), e -> {
			browseItemsMenu.getFilters().add(FilterSearchType.SEARCH.of("Enchanted items", product -> filter(product.getItem(), item -> {
				if (item.getType().equals(Material.ENCHANTED_BOOK)) {
					EnchantmentStorageMeta book = (EnchantmentStorageMeta) item.getItemMeta();
					return book != null && !book.getStoredEnchants().isEmpty();
				} else {
					return !item.getEnchantments().isEmpty();
				}
			})));
			browseItemsMenu.open(player);
		}));

		contents.set(1, 7, ClickableItem.from(nameItem(Material.DIAMOND_SWORD, "&6Search for tools,", "&6weapons and armor"), e -> {
			browseItemsMenu.getFilters().add(FilterSearchType.SEARCH.of("Tools, weapons and armor", product -> filter(product.getItem(), MaterialTag.TOOLS_WEAPONS_ARMOR)));
			browseItemsMenu.open(player);
		}));

		contents.set(2, 2, ClickableItem.from(nameItem(Material.POTION, "&6Search for potion materials"), e -> {
			browseItemsMenu.getFilters().add(FilterSearchType.SEARCH.of("Potion materials", product -> filter(product.getItem(), MaterialTag.POTION_MATERIALS)));
			browseItemsMenu.open(player);
		}));

		contents.set(2, 4, ClickableItem.from(nameItem(Material.SHROOMLIGHT, "&6Search for light sources"), e -> {
			browseItemsMenu.getFilters().add(FilterSearchType.SEARCH.of("Light sources", product -> filter(product.getItem(), MaterialTag.LIGHT_SOURCES)));
			browseItemsMenu.open(player);
		}));

		contents.set(2, 6, ClickableItem.from(nameItem(Material.CYAN_CONCRETE, "&6Search for colored materials"), e -> {
			browseItemsMenu.getFilters().add(FilterSearchType.SEARCH.of("Colored materials", product -> filter(product.getItem(), MaterialTag.COLORABLE)));
			browseItemsMenu.open(player);
		}));

		contents.set(3, 1, ClickableItem.from(nameItem(Material.BLUE_ORCHID, "&6Search for flora"), e -> {
			browseItemsMenu.getFilters().add(FilterSearchType.SEARCH.of("Flora", product -> filter(product.getItem(), MaterialTag.FLORA)));
			browseItemsMenu.open(player);
		}));

		contents.set(3, 3, ClickableItem.from(nameItem(Material.OAK_PLANKS, "&6Search for wooden materials"), e -> {
			browseItemsMenu.getFilters().add(FilterSearchType.SEARCH.of("Wood", product -> filter(product.getItem(), MaterialTag.ALL_WOOD)));
			browseItemsMenu.open(player);
		}));

		contents.set(3, 5, ClickableItem.from(nameItem(Material.DIAMOND_ORE, "&6Search for minerals"), e -> {
			browseItemsMenu.getFilters().add(FilterSearchType.SEARCH.of("Minerals", product -> filter(product.getItem(), MaterialTag.ALL_MINERALS)));
			browseItemsMenu.open(player);
		}));

		contents.set(3, 7, ClickableItem.from(nameItem(Material.MUSIC_DISC_PIGSTEP, "&6Search for musical items"), e -> {
			browseItemsMenu.getFilters().add(FilterSearchType.SEARCH.of("Musical items", product -> filter(product.getItem(), MaterialTag.MUSIC)));
			browseItemsMenu.open(player);
		}));

		contents.set(4, 2, ClickableItem.from(nameItem(Material.NETHERRACK, "&6Search for nether materials"), e -> {
			browseItemsMenu.getFilters().add(FilterSearchType.SEARCH.of("Nether materials", product -> filter(product.getItem(), MaterialTag.ALL_NETHER)));
			browseItemsMenu.open(player);
		}));

		contents.set(4, 4, ClickableItem.from(nameItem(Material.END_STONE, "&6Search for end materials"), e -> {
			browseItemsMenu.getFilters().add(FilterSearchType.SEARCH.of("End materials", product -> filter(product.getItem(), MaterialTag.ALL_END)));
			browseItemsMenu.open(player);
		}));

		contents.set(4, 6, ClickableItem.from(nameItem(Material.PRISMARINE_BRICKS, "&6Search for ocean materials"), e -> {
			browseItemsMenu.getFilters().add(FilterSearchType.SEARCH.of("Ocean materials", product -> filter(product.getItem(), MaterialTag.ALL_OCEAN)));
			browseItemsMenu.open(player);
		}));
	}

	public static boolean filter(ItemStack item, MaterialTag materialTag) {
		if (materialTag.isTagged(item.getType()))
			return true;

		for (ItemStack content : ItemUtils.getShulkerContents(item))
			if (materialTag.isTagged(content.getType()))
				return true;

		return false;
	}

	public static boolean filter(ItemStack item, Predicate<ItemStack> predicate) {
		if (predicate.test(item))
			return true;

		for (ItemStack content : ItemUtils.getShulkerContents(item))
			if (predicate.test(content))
				return true;

		return false;
	}

}
