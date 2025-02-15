package gg.projecteden.nexus.features.shops.providers;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.SignMenuFactory;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.shops.Shops;
import gg.projecteden.nexus.features.shops.providers.common.ShopMenuFunctions.FilterSearchType;
import gg.projecteden.nexus.features.shops.providers.common.ShopProvider;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.function.Predicate;

@Title("&0Search Items")
public class SearchProductsProvider extends ShopProvider {

	public SearchProductsProvider(ShopProvider previousMenu) {
		this.previousMenu = previousMenu;
	}

	@Override
	public void init() {
		super.init();

		BrowseProductsProvider browseItemsMenu;
		if (previousMenu instanceof BrowseProductsProvider)
			browseItemsMenu = (BrowseProductsProvider) previousMenu;
		else
			browseItemsMenu = new BrowseProductsProvider(this);

		contents.set(1, 1, ClickableItem.of(Material.NAME_TAG, "&6Search by item name", e -> Nexus.getSignMenuFactory()
			.lines("", SignMenuFactory.ARROWS, "Enter a", "search term")
			.prefix(Shops.PREFIX)
			.onError(() -> open(viewer))
			.response(lines -> {
				String input = StringUtils.stripColor(lines[0]);
				if (input.length() > 0) {
					browseItemsMenu.getFilters().add(FilterSearchType.SEARCH.of(input));
					browseItemsMenu.open(viewer);
				} else
					open(viewer);
			}).open(viewer)));

		contents.set(1, 3, ClickableItem.of(Material.APPLE, "&6Search for food", e -> {
			browseItemsMenu.getFilters().add(FilterSearchType.SEARCH.of("Food", product -> filter(product.getItem(), MaterialTag.FOODS)));
			browseItemsMenu.open(viewer);
		}));

		contents.set(1, 5, ClickableItem.of(Material.ENCHANTED_BOOK, "&6Search for enchanted items", e -> {
			browseItemsMenu.getFilters().add(FilterSearchType.SEARCH.of("Enchanted items", product -> filter(product.getItem(), item -> {
				if (item.getType().equals(Material.ENCHANTED_BOOK)) {
					EnchantmentStorageMeta book = (EnchantmentStorageMeta) item.getItemMeta();
					return book != null && !book.getStoredEnchants().isEmpty();
				} else {
					return !item.getEnchantments().isEmpty();
				}
			})));
			browseItemsMenu.open(viewer);
		}));

		contents.set(1, 7, ClickableItem.of(Material.DIAMOND_SWORD, "&6Search for tools,", "&6weapons and armor", e -> {
			browseItemsMenu.getFilters().add(FilterSearchType.SEARCH.of("Tools, weapons and armor", product -> filter(product.getItem(), MaterialTag.TOOLS_WEAPONS_ARMOR)));
			browseItemsMenu.open(viewer);
		}));

		contents.set(2, 2, ClickableItem.of(Material.POTION, "&6Search for potion materials", e -> {
			browseItemsMenu.getFilters().add(FilterSearchType.SEARCH.of("Potion materials", product -> filter(product.getItem(), MaterialTag.POTION_MATERIALS)));
			browseItemsMenu.open(viewer);
		}));

		contents.set(2, 4, ClickableItem.of(Material.SHROOMLIGHT, "&6Search for light sources", e -> {
			browseItemsMenu.getFilters().add(FilterSearchType.SEARCH.of("Light sources", product -> filter(product.getItem(), MaterialTag.DECORATIVE_LIGHT_SOURCES)));
			browseItemsMenu.open(viewer);
		}));

		contents.set(2, 6, ClickableItem.of(Material.CYAN_CONCRETE, "&6Search for colored materials", e -> {
			browseItemsMenu.getFilters().add(FilterSearchType.SEARCH.of("Colored materials", product -> filter(product.getItem(), MaterialTag.COLORABLE)));
			browseItemsMenu.open(viewer);
		}));

		contents.set(3, 1, ClickableItem.of(Material.BLUE_ORCHID, "&6Search for flora", e -> {
			browseItemsMenu.getFilters().add(FilterSearchType.SEARCH.of("Flora", product -> filter(product.getItem(), MaterialTag.FLORA)));
			browseItemsMenu.open(viewer);
		}));

		contents.set(3, 3, ClickableItem.of(Material.OAK_PLANKS, "&6Search for wooden materials", e -> {
			browseItemsMenu.getFilters().add(FilterSearchType.SEARCH.of("Wood", product -> filter(product.getItem(), MaterialTag.ALL_WOOD)));
			browseItemsMenu.open(viewer);
		}));

		contents.set(3, 5, ClickableItem.of(Material.DIAMOND_ORE, "&6Search for minerals", e -> {
			browseItemsMenu.getFilters().add(FilterSearchType.SEARCH.of("Minerals", product -> filter(product.getItem(), MaterialTag.ALL_MINERALS)));
			browseItemsMenu.open(viewer);
		}));

		contents.set(3, 7, ClickableItem.of(Material.MUSIC_DISC_PIGSTEP, "&6Search for musical items", e -> {
			browseItemsMenu.getFilters().add(FilterSearchType.SEARCH.of("Musical items", product -> filter(product.getItem(), MaterialTag.MUSIC)));
			browseItemsMenu.open(viewer);
		}));

		contents.set(4, 2, ClickableItem.of(Material.NETHERRACK, "&6Search for nether materials", e -> {
			browseItemsMenu.getFilters().add(FilterSearchType.SEARCH.of("Nether materials", product -> filter(product.getItem(), MaterialTag.ALL_NETHER)));
			browseItemsMenu.open(viewer);
		}));

		contents.set(4, 4, ClickableItem.of(Material.END_STONE, "&6Search for end materials", e -> {
			browseItemsMenu.getFilters().add(FilterSearchType.SEARCH.of("End materials", product -> filter(product.getItem(), MaterialTag.ALL_END)));
			browseItemsMenu.open(viewer);
		}));

		contents.set(4, 6, ClickableItem.of(Material.PRISMARINE_BRICKS, "&6Search for ocean materials", e -> {
			browseItemsMenu.getFilters().add(FilterSearchType.SEARCH.of("Ocean materials", product -> filter(product.getItem(), MaterialTag.ALL_OCEAN)));
			browseItemsMenu.open(viewer);
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
