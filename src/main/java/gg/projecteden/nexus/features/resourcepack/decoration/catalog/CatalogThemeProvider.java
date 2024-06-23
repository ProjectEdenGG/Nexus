package gg.projecteden.nexus.features.resourcepack.decoration.catalog;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.ItemClickData;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationLang;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationType;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationType.CategoryTree;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.catalog.Catalog.Tab;
import gg.projecteden.nexus.features.resourcepack.decoration.catalog.Catalog.Theme;
import gg.projecteden.nexus.models.banker.Transaction.TransactionCause;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NonNull;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CatalogThemeProvider extends InventoryProvider {
	InventoryProvider previousMenu;
	Catalog.Theme catalogTheme;
	CategoryTree currentTree;

	public CatalogThemeProvider(@NonNull Catalog.Theme catalogTheme, @NonNull CategoryTree tree, @Nullable InventoryProvider previousMenu) {
		this(catalogTheme, previousMenu);
		this.currentTree = tree;
	}

	public CatalogThemeProvider(@NonNull Catalog.Theme catalogTheme, @Nullable InventoryProvider previousMenu) {
		this.catalogTheme = catalogTheme;
		this.previousMenu = previousMenu;
	}

	@Override
	public String getTitle() {
		String catalogName = StringUtils.camelCase(catalogTheme);
		String tabName = StringUtils.camelCase(currentTree.getTabParent());
		tabName = tabName.replaceFirst(catalogName, "").trim();

		if (currentTree.isRoot())
			return "Catalog | " + catalogName;

		return catalogName + " | " + tabName;
	}

	@Override
	public void onPageTurn(Player viewer) {
		DecorationUtils.getSoundBuilder(Sound.ITEM_BOOK_PAGE_TURN).category(SoundCategory.PLAYERS).location(viewer).play();
	}

	@Override
	public void onClose(InventoryCloseEvent event, List<ItemStack> contents) {
		DecorationUtils.getSoundBuilder(Sound.ITEM_BOOK_PUT).category(SoundCategory.PLAYERS).location(viewer).play();
	}

	@Override
	public void init() {
		addBackOrCloseItem(previousMenu);

		CatalogProvider.addInfoItems(contents, viewer);

		List<ClickableItem> items = new ArrayList<>();

		if (currentTree == null)
			currentTree = DecorationType.getCategoryTree();

		// Add Children Folders

		String catalogName = StringUtils.camelCase(catalogTheme);
		for (CategoryTree child : currentTree.getTabChildren()) {
			String tabName = StringUtils.camelCase(child.getTabParent());
			tabName = tabName.replaceFirst(catalogName, "").trim();

			if (child.isRoot() || child.isInvisible()) {
				DecorationLang.debug(viewer, "Skipping " + tabName + " -> is root | invisible");
				continue;
			}

			List<DecorationType> decorationTypes = child.getDecorationTypes();
			if (decorationTypes.isEmpty() && child.getTabChildren().isEmpty()) {
				DecorationLang.debug(viewer, "Skipping " + tabName + " -> is empty 1");
				continue;
			}

			if (child.getTabParent() != Tab.COUNTERS_MENU && getClickableTabItems(child, catalogTheme).isEmpty()) {
				boolean skipTab = true;
				for (CategoryTree tabChild : child.getTabChildren()) {
					if (!getClickableTabItems(tabChild, catalogTheme).isEmpty()) {
						skipTab = false;
						break;
					}
				}

				if (skipTab) {
					DecorationLang.debug(viewer, "Skipping " + tabName + " -> is empty 2");
					continue;
				}
			}

			ItemBuilder icon = child.getTabParent().getIcon().name("&3" + tabName).glow();
			if (child.getTabParent() == Tab.COUNTERS_MENU)
				icon.name("&3Counters");

			Consumer<ItemClickData> consumer = e -> Catalog.openCatalog(viewer, catalogTheme, child, this);
			if (child.getTabParent() == Tab.COUNTERS_MENU)
				consumer = e -> Catalog.openCountersCatalog(viewer, catalogTheme, child, this);

			items.add(ClickableItem.of(icon.build(), consumer));
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
		for (ItemStack itemStack : getBuyableDecoration(tree, theme)) {
			clickableItems.add(ClickableItem.of(itemStack, e -> Catalog.tryBuySurvivalItem(viewer, itemStack, TransactionCause.DECORATION_CATALOG)));
		}

		return clickableItems;
	}

	private List<ItemStack> getBuyableDecoration(CategoryTree tree, Theme theme) {
		if (tree.isInvisible())
			return new ArrayList<>();

		return tree.getDecorationTypes().stream()
				.filter(type -> type.getTypeConfig().theme() == theme)
				.filter(type -> type.getTypeConfig().price() != -1)
				.filter(type -> !type.getTypeConfig().unbuyable())
				.map(type -> type.getConfig().getCatalogItem(viewer))
				.toList();
	}
}
