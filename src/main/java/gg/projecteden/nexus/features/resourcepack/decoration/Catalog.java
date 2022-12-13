package gg.projecteden.nexus.features.resourcepack.decoration;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
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
		TABLES(CustomMaterial.TABLE_WOODEN_1X1.getItem(), StainChoice.OAK.getColor()),
		CHAIRS(CustomMaterial.CHAIR_WOODEN_BASIC.getItem(), StainChoice.OAK.getColor()),
		ART(CustomMaterial.ART_PAINTING_SKYBLOCK.getItem()),
		FOOD(CustomMaterial.FOOD_BREAD_LOAF.getItem()),
		POTIONS(CustomMaterial.POTION_FILLED_GROUP_RANDOM_2.getItem(), DyeChoice.WHITE.getColor()),
		KITCHENWARE(CustomMaterial.KITCHENWARE_MIXING_BOWL.getItem()),
		WINDCHIMES(CustomMaterial.WINDCHIMES_COPPER.getItem()),
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

	public static void openCatalog(Player viewer, Theme theme, @Nullable Tab tab, @Nullable InventoryProvider previousMenu) {
		tab = tab == null ? Tab.NONE : tab;
		new CatalogProvider(theme, tab, previousMenu).open(viewer);
	}

	public static class CatalogProvider extends InventoryProvider {
		InventoryProvider previousMenu;
		Catalog.Theme catalogTheme;
		Catalog.Tab catalogTab = Tab.NONE;

		public CatalogProvider(@NonNull Theme catalogTheme, @NonNull Tab catalogTab, @Nullable InventoryProvider previousMenu) {
			this(catalogTheme, previousMenu);
			this.catalogTab = catalogTab;
		}

		public CatalogProvider(@NonNull Theme catalogTheme, @Nullable InventoryProvider previousMenu) {
			this.catalogTheme = catalogTheme;
			this.previousMenu = previousMenu;
		}

		@Override
		public String getTitle() {
			String catalogName = StringUtils.camelCase(catalogTheme);
			String tabName = StringUtils.camelCase(catalogTab);
			if (catalogTab == Tab.NONE)
				tabName = "Main";

			return "Catalog: " + catalogName + " - " + tabName;
		}

		@Override
		public void init() {
			addBackOrCloseItem(previousMenu);

			List<ClickableItem> items = new ArrayList<>();

			if (catalogTab == Tab.NONE) {
				for (Tab tab : Tab.values()) {
					if (tab == Tab.NONE || tab == Tab.INVISIBLE)
						continue;

					if (getDecoration(tab, catalogTheme).size() == 0)
						continue;

					ItemBuilder icon = tab.getIcon()
						.name(StringUtils.camelCase(tab))
						.glow();

					items.add(ClickableItem.of(icon.build(), e -> openCatalog(viewer, catalogTheme, tab, this)));
				}

				if (!items.isEmpty()) {
					while (items.size() % 9 != 0)
						items.add(ClickableItem.NONE);

					for (int i = 0; i < 9; i++)
						items.add(ClickableItem.NONE);
				}

				items.addAll(getClickableTabItems(Tab.NONE, catalogTheme));
			} else {
				items.addAll(getClickableTabItems(catalogTab, catalogTheme));
			}

			paginator().items(items).build();
		}

		private List<ClickableItem> getClickableTabItems(Tab tab, Theme theme) {
			if (tab == Tab.INVISIBLE)
				return new ArrayList<>();

			List<ClickableItem> clickableItems = new ArrayList<>();
			for (ItemStack decoration : getDecoration(tab, theme)) {
				clickableItems.add(ClickableItem.of(decoration, e -> PlayerUtils.giveItem(viewer, decoration)));
			}

			return clickableItems;
		}

		private List<ItemStack> getDecoration(Tab tab, Theme theme) {
			if (tab == Tab.INVISIBLE)
				return new ArrayList<>();

			return DecorationType.getBy(tab, theme)
				.stream()
				.map(decorationType -> decorationType.getConfig().getItem())
				.toList();
		}
	}


}
