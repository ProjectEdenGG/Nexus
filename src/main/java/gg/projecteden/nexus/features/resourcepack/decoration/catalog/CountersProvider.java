package gg.projecteden.nexus.features.resourcepack.decoration.catalog;

import gg.projecteden.api.common.utils.EnumUtils.IterableEnum;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationType;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationType.CategoryTree;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Counter;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Counter.CounterMaterial;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Counter.HandleType;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.workbenches.dyestation.ColorChoice;
import gg.projecteden.nexus.models.banker.Transaction.TransactionCause;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static gg.projecteden.api.common.utils.StringUtils.camelCase;

public class CountersProvider extends InventoryProvider {
	@NonNull InventoryProvider previousMenu;
	@NonNull Catalog.Theme catalogTheme;
	@NonNull CategoryTree currentTree;
	@NonNull HandleFilter handleFilter = HandleFilter.ALL;
	@NonNull CounterFilter counterFilter = CounterFilter.ALL;

	public CountersProvider(@NonNull Catalog.Theme catalogTheme, @NonNull CategoryTree tree, @NonNull InventoryProvider previousMenu,
							@NotNull HandleFilter handleFilter, @NotNull CounterFilter counterFilter) {
		this(catalogTheme, tree, previousMenu);

		this.handleFilter = handleFilter;
		this.counterFilter = counterFilter;
	}

	public CountersProvider(@NonNull Catalog.Theme catalogTheme, @NonNull CategoryTree tree, @NonNull InventoryProvider previousMenu) {
		this.catalogTheme = catalogTheme;
		this.currentTree = tree;
		this.previousMenu = previousMenu;
	}

	@Override
	public String getTitle() {
		return StringUtils.camelCase(catalogTheme) + " | Counters";
	}

	@Override
	public void onPageTurn(Player viewer) {
		new SoundBuilder(Sound.ITEM_BOOK_PAGE_TURN).location(viewer).play();
	}

	@Override
	public void onClose(InventoryCloseEvent event, List<ItemStack> contents) {
		new SoundBuilder(Sound.ITEM_BOOK_PUT).location(viewer).play();
	}

	@Override
	public void init() {
		addBackItem(previousMenu);

		paginator().items(new ArrayList<>(getBuyableFilteredItems())).useGUIArrows().build();

		// Add filter items
		contents.set(5, 3, ClickableItem.of(handleFilter.getFilterItem(), e ->
			new CountersProvider(catalogTheme, currentTree, previousMenu, handleFilter.nextWithLoop(), counterFilter).open(viewer)
		));

		contents.set(5, 5, ClickableItem.of(counterFilter.getFilterItem(), e ->
			new CountersProvider(catalogTheme, currentTree, previousMenu, handleFilter, counterFilter.nextWithLoop()).open(viewer)
		));


	}

	private List<ClickableItem> getBuyableFilteredItems() {
		List<DecorationType> decorationTypes = new ArrayList<>(currentTree.getDecorationTypes());
		decorationTypes.addAll(getChildDecorations(currentTree, decorationTypes));

		Set<DecorationType> filteredTypes = decorationTypes.stream()
				.filter(type -> type.getTypeConfig().theme() == catalogTheme)
				.filter(type -> handleFilter.applies(type))
				.filter(type -> counterFilter.applies(type))
				.filter(type -> type.getTypeConfig().price() != -1)
				.filter(type -> !type.getTypeConfig().unbuyable())
				.collect(Collectors.toSet());

		List<ClickableItem> clickableItems = new ArrayList<>();
		filteredTypes.stream()
			.sorted(Comparator.comparing(type -> type.getConfig().getName()))
			.map(type -> type.getConfig().getCatalogItem(viewer))
			.toList()
			.forEach(itemStack -> clickableItems.add(ClickableItem.of(itemStack, e -> Catalog.buyItem(viewer, itemStack, TransactionCause.DECORATION_CATALOG))));

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
				.lore("&7⬇ " + camelCase(this.previousWithLoop().name()))
				.lore("&e⬇ " + camelCase(this.name()))
				.lore("&7⬇ " + camelCase(this.nextWithLoop().name()));
		}

		boolean applies(DecorationType type);
	}

	@AllArgsConstructor
	private enum HandleFilter implements FilterType {
		ALL(CustomMaterial.HANDLE_ALL, HandleType.values()),
		STEEL(CustomMaterial.HANDLE_STEEL, HandleType.STEEL),
		BRASS(CustomMaterial.HANDLE_BRASS, HandleType.BRASS),
		BLACK(CustomMaterial.HANDLE_BLACK, HandleType.BLACK);

		final CustomMaterial customMaterial;
		@Getter
		final List<HandleType> handleTypes;

		HandleFilter(CustomMaterial itemMaterial, HandleType... handleTypes) {
			this.customMaterial = itemMaterial;
			this.handleTypes = List.of(handleTypes);
		}

		@Override
		public ItemBuilder getItem() {
			return new ItemBuilder(customMaterial);
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
		ALL(CustomMaterial.COUNTER_ALL, CounterMaterial.values()),
		MARBLE(CustomMaterial.COUNTER_MARBLE, CounterMaterial.MARBLE),
		STONE(CustomMaterial.COUNTER_STONE, CounterMaterial.STONE),
		SOAPSTONE(CustomMaterial.COUNTER_SOAPSTONE, CounterMaterial.SOAPSTONE),
		WOODEN(CustomMaterial.COUNTER_WOODEN, CounterMaterial.WOODEN);

		final CustomMaterial customMaterial;
		@Getter
		final List<CounterMaterial> counterMaterials;

		CounterFilter(CustomMaterial itemMaterial, CounterMaterial... counterMaterials) {
			this.customMaterial = itemMaterial;
			this.counterMaterials = List.of(counterMaterials);
		}

		@Override
		public ItemBuilder getItem() {
			ItemBuilder item = new ItemBuilder(customMaterial);
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
