package gg.projecteden.nexus.features.resourcepack.decoration.catalog;

import gg.projecteden.api.common.utils.EnumUtils.IterableEnum;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationType;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationType.CategoryTree;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.store.DecorationStoreCurrencyType;
import gg.projecteden.nexus.features.resourcepack.decoration.store.DecorationStoreType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Counter;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Counter.CounterMaterial;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Counter.HandleType;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.features.workbenches.dyestation.ColorChoice;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CountersProvider extends InventoryProvider {
	@NonNull InventoryProvider previousMenu;
	@NonNull Catalog.Theme catalogTheme;
	@NonNull CategoryTree currentTree;
	@NonNull HandleFilter handleFilter = HandleFilter.ALL;
	@NonNull CounterFilter counterFilter = CounterFilter.ALL;
	DecorationStoreCurrencyType currency;

	public CountersProvider(@NonNull Catalog.Theme catalogTheme, @NonNull CategoryTree tree, @NonNull InventoryProvider previousMenu,
							@NotNull HandleFilter handleFilter, @NotNull CounterFilter counterFilter, DecorationStoreCurrencyType currency) {
		this(catalogTheme, tree, currency, previousMenu);

		this.handleFilter = handleFilter;
		this.counterFilter = counterFilter;
	}

	public CountersProvider(@NonNull Catalog.Theme catalogTheme, @NonNull CategoryTree tree, DecorationStoreCurrencyType currency, @NonNull InventoryProvider previousMenu) {
		this.catalogTheme = catalogTheme;
		this.currentTree = tree;
		this.previousMenu = previousMenu;
		this.currency = currency;
	}

	@Override
	public String getTitle() {
		return StringUtils.camelCase(catalogTheme) + " | Counters";
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
		addBackItem(previousMenu);

		CatalogProvider.addInfoItems(contents, viewer);

		paginator().items(new ArrayList<>(getBuyableFilteredItems())).useGUIArrows().build();

		// Add filter items
		contents.set(5, 3, ClickableItem.of(handleFilter.getFilterItem(), e ->
				new CountersProvider(catalogTheme, currentTree, previousMenu, handleFilter.nextWithLoop(), counterFilter, currency).open(viewer)
		));

		contents.set(5, 5, ClickableItem.of(counterFilter.getFilterItem(), e ->
				new CountersProvider(catalogTheme, currentTree, previousMenu, handleFilter, counterFilter.nextWithLoop(), currency).open(viewer)
		));


	}

	private List<ClickableItem> getBuyableFilteredItems() {
		List<DecorationType> decorationTypes = new ArrayList<>(currentTree.getDecorationTypes());
		decorationTypes.addAll(getChildDecorations(currentTree, decorationTypes));

		Set<DecorationType> filteredTypes = decorationTypes.stream()
				.filter(type -> type.getTypeConfig().theme() == catalogTheme)
				.filter(type -> handleFilter.applies(type))
				.filter(type -> counterFilter.applies(type))
				.filter(type -> type.getTypeConfig().money() != -1)
				.filter(type -> !type.getTypeConfig().unbuyable())
				.collect(Collectors.toSet());

		List<ClickableItem> clickableItems = new ArrayList<>();
		List<DecorationConfig> configs = filteredTypes.stream()
			.sorted(Comparator.comparing(type -> type.getConfig().getName()))
			.map(DecorationType::getConfig)
			.toList();

		for (DecorationConfig config : configs) {
			ItemStack displayItem = config.getPricedCatalogItem(viewer, currency, DecorationStoreType.CATALOG);
			ClickableItem clickableItem = ClickableItem.of(displayItem, e -> {
				Catalog.tryBuySurvivalItem(viewer, config, config.getItem(), DecorationStoreType.CATALOG);
			});

			clickableItems.add(clickableItem);
		}

		return clickableItems;
	}

	private List<DecorationType> getChildDecorations(CategoryTree tree, List<DecorationType> decorations) {
		List<CategoryTree> children = tree.getTabChildren();
		if (children.isEmpty())
			return decorations;

		for (CategoryTree tabChild : children) {
			decorations.addAll(tabChild.getDecorationTypes());
			getChildDecorations(tabChild, decorations);
		}

		return decorations;
	}


	private interface FilterType extends IterableEnum {
		String name();

		ItemBuilder getItem();

		default ItemBuilder getFilterItem() {
			return getItem()
				.name("&6Filter by: ")
				.lore("&7⬇ " + StringUtils.camelCase(this.previousWithLoop().name()))
				.lore("&e⬇ " + StringUtils.camelCase(this.name()))
				.lore("&7⬇ " + StringUtils.camelCase(this.nextWithLoop().name()));
		}

		boolean applies(DecorationType type);
	}

	@AllArgsConstructor
	private enum HandleFilter implements FilterType {
		ALL(ItemModelType.HANDLE_ALL, HandleType.values()),
		STEEL(ItemModelType.HANDLE_STEEL, HandleType.STEEL),
		BRASS(ItemModelType.HANDLE_BRASS, HandleType.BRASS),
		BLACK(ItemModelType.HANDLE_BLACK, HandleType.BLACK);

		final ItemModelType itemModelType;
		@Getter
		final List<HandleType> handleTypes;

		HandleFilter(ItemModelType itemModelType, HandleType... handleTypes) {
			this.itemModelType = itemModelType;
			this.handleTypes = List.of(handleTypes);
		}

		@Override
		public ItemBuilder getItem() {
			return new ItemBuilder(itemModelType);
		}

		@Override
		public boolean applies(DecorationType type) {
			if (!(type.getConfig() instanceof Counter counter))
				return false;

			return getHandleTypes().contains(counter.getHandleType());
		}
	}

	@AllArgsConstructor
	private enum CounterFilter implements FilterType {
		ALL(ItemModelType.COUNTER_ALL, CounterMaterial.values()),
		MARBLE(ItemModelType.COUNTER_MARBLE, CounterMaterial.MARBLE),
		STONE(ItemModelType.COUNTER_STONE, CounterMaterial.STONE),
		SOAPSTONE(ItemModelType.COUNTER_SOAPSTONE, CounterMaterial.SOAPSTONE),
		WOODEN(ItemModelType.COUNTER_WOODEN, CounterMaterial.WOODEN);

		final ItemModelType itemModelType;
		@Getter
		final List<CounterMaterial> counterMaterials;

		CounterFilter(ItemModelType itemModelType, CounterMaterial... counterMaterials) {
			this.itemModelType = itemModelType;
			this.counterMaterials = List.of(counterMaterials);
		}

		@Override
		public ItemBuilder getItem() {
			ItemBuilder item = new ItemBuilder(itemModelType);
			if (this == WOODEN)
				item.dyeColor(ColorChoice.StainChoice.OAK.getColor());

			return item;
		}

		@Override
		public boolean applies(DecorationType type) {
			if (!(type.getConfig() instanceof Counter counter))
				return false;

			return getCounterMaterials().contains(counter.getCounterMaterial());
		}
	}
}
