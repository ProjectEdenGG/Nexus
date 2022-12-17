package gg.projecteden.nexus.features.resourcepack.decoration.catalog;

import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationType;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationType.CategoryTree;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.workbenches.DyeStation.DyeStationMenu.DyeChoice;
import gg.projecteden.nexus.features.workbenches.DyeStation.DyeStationMenu.StainChoice;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

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

		COUNTERS_MENU(CustomMaterial.COUNTER_BLACK_SOAPSTONE_CABINET.getItem(), StainChoice.OAK.getColor()),

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

	public static void openCountersCatalog(Player viewer, Theme theme, @NonNull CategoryTree tree, @NonNull InventoryProvider previousMenu) {
		new CountersProvider(theme, tree, previousMenu).open(viewer);
	}


}
