package gg.projecteden.nexus.features.resourcepack.decoration;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationType.CategoryTree;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.workbenches.DyeStation.DyeStationMenu.DyeChoice;
import gg.projecteden.nexus.features.workbenches.DyeStation.DyeStationMenu.StainChoice;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Catalog {

	@AllArgsConstructor
	@NoArgsConstructor
	public enum Tab {
		INVISIBLE,

		NONE,
		FURNITURE(CustomMaterial.COUNTER_BLACK_SOAPSTONE_CABINET.getItem(), StainChoice.OAK.getColor()),
		APPLIANCES(CustomMaterial.APPLIANCE_FRIDGE_MAGNETS.getItem(), DyeChoice.WHITE.getColor()),
		CHAIRS(CustomMaterial.CHAIR_WOODEN_BASIC.getItem(), StainChoice.OAK.getColor()),
		STOOLS(CustomMaterial.STOOL_WOODEN_BASIC.getItem(), StainChoice.OAK.getColor()),
		STUMPS(CustomMaterial.STUMP_OAK.getItem()),
		TABLES(CustomMaterial.TABLE_WOODEN_1X1.getItem(), StainChoice.OAK.getColor()),
		ART(CustomMaterial.ART_PAINTING_SKYBLOCK.getItem()),
		FOOD(CustomMaterial.FOOD_BREAD_LOAF.getItem()),
		POTIONS(CustomMaterial.POTION_FILLED_GROUP_RANDOM_2.getItem(), DyeChoice.WHITE.getColor()),
		KITCHENWARE(CustomMaterial.KITCHENWARE_MIXING_BOWL.getItem()),
		WINDCHIMES(CustomMaterial.WINDCHIMES_COPPER.getItem()),

		COUNTERS(CustomMaterial.COUNTER_BLACK_SOAPSTONE_CABINET.getItem(), StainChoice.OAK.getColor()),

		MARBLE_COUNTER(CustomMaterial.COUNTER_MARBLE.getItem()),
		STONE_COUNTER(CustomMaterial.COUNTER_STONE.getItem()),
		SOAPSTONE_COUNTER(CustomMaterial.COUNTER_SOAPSTONE.getItem()),
		WOODEN_COUNTER(CustomMaterial.COUNTER_WOODEN.getItem(), StainChoice.OAK.getColor()),

		CABINETS(CustomMaterial.CABINET_BLACK_WOODEN.getItem(), StainChoice.OAK.getColor()),

		BLACK_HANDLES(CustomMaterial.HANDLE_BLACK.getItem()),
		STEEL_HANDLES(CustomMaterial.HANDLE_STEEL.getItem()),
		BRASS_HANDLES(CustomMaterial.HANDLE_BRASS.getItem()),
		;

		ItemStack icon = new ItemStack(Material.AIR);
		Color color = null;

		Tab(ItemStack icon) {
			this.icon = icon;
			this.color = null;
		}

		public ItemBuilder getIcon() {
			ItemBuilder item = new ItemBuilder(icon);
			if (color == null)
				return item;

			return item.dyeColor(color);
		}
	}

	@AllArgsConstructor
	public enum Theme {
		GENERAL(CustomMaterial.DECORATION_CATALOG_GENERAL),
		HOLIDAY(CustomMaterial.DECORATION_CATALOG_HOLIDAY),
		SPOOKY(CustomMaterial.DECORATION_CATALOG_SPOOKY),
		;

		final CustomMaterial customMaterial;

		public ItemStack getNamedItem() {
			return new ItemBuilder(customMaterial)
				.name("Decoration Catalog: " + StringUtils.camelCase(this))
				.build();
		}
	}

	public static void openCatalog(Player viewer, Theme theme, @Nullable CategoryTree tree, @Nullable InventoryProvider previousMenu) {
		tree = tree == null ? DecorationType.getCategoryTree() : tree;
		new CatalogProvider(theme, tree, previousMenu).open(viewer);
	}

	public static class CatalogProvider extends InventoryProvider {
		InventoryProvider previousMenu;
		Catalog.Theme catalogTheme;
		CategoryTree currentTree;

		public CatalogProvider(@NonNull Theme catalogTheme, @NonNull CategoryTree tree, @Nullable InventoryProvider previousMenu) {
			this(catalogTheme, previousMenu);
			this.currentTree = tree;
		}

		public CatalogProvider(@NonNull Theme catalogTheme, @Nullable InventoryProvider previousMenu) {
			this.catalogTheme = catalogTheme;
			this.previousMenu = previousMenu;
		}

		@Override
		public String getTitle() {
			String catalogName = StringUtils.camelCase(catalogTheme);
			String tabName = StringUtils.camelCase(currentTree.getTabParent());
			if (currentTree.isRoot())
				return "Catalog: " + catalogName;

			return catalogName + ": " + tabName;
		}

		@Override
		public void init() {
			addBackOrCloseItem(previousMenu);

			List<ClickableItem> items = new ArrayList<>();

			if (currentTree == null)
				currentTree = DecorationType.getCategoryTree();

			// Add Children Folders
			List<CategoryTree> children = currentTree.getTabChildren();
			for (CategoryTree child : children) {
				if (child.isRoot() || child.isInvisible())
					continue;

				List<DecorationType> decorationTypes = child.getDecorationTypes();
				if (decorationTypes.isEmpty() && child.getTabChildren().isEmpty())
					continue;

				ItemBuilder icon = child.getTabParent().getIcon()
					.name(StringUtils.camelCase(child.getTabParent()))
					.glow();

				items.add(ClickableItem.of(icon.build(), e -> openCatalog(viewer, catalogTheme, child, this)));
			}

			// Separation
			if (!items.isEmpty()) {
				while (items.size() % 9 != 0)
					items.add(ClickableItem.NONE);

				for (int i = 0; i < 9; i++)
					items.add(ClickableItem.NONE);
			}

			// Add Items
			items.addAll(getClickableTabItems(currentTree, catalogTheme));

			paginator().items(items).useGUIArrows().build();
		}

		private List<ClickableItem> getClickableTabItems(CategoryTree tree, Theme theme) {
			if (tree.isInvisible())
				return new ArrayList<>();

			List<ClickableItem> clickableItems = new ArrayList<>();
			for (ItemStack decoration : getDecoration(tree, theme)) {
				clickableItems.add(ClickableItem.of(decoration, e -> PlayerUtils.giveItem(viewer, decoration)));
			}

			return clickableItems;
		}

		private List<ItemStack> getDecoration(CategoryTree tree, Theme theme) {
			if (tree.isInvisible())
				return new ArrayList<>();

			return tree.getDecorationTypes().stream()
				.filter(type -> type.getTheme() == theme)
				.map(type -> type.getConfig().getItem())
				.toList();
		}
	}


}
