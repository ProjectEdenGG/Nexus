package gg.projecteden.nexus.features.minigames.menus.lobby;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.ItemClickData;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicSubGroup;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicType;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static gg.projecteden.api.common.utils.StringUtils.camelCase;

@RequiredArgsConstructor
public class MechanicSubGroupMenu extends InventoryProvider {
	private final MechanicSubGroup group;

	private static final BiFunction<MechanicType, ItemBuilder, ItemBuilder> itemBuilder = (mechanic, item) -> {
		item.name("&6&l" + mechanic.get().getName());
		item.lore("");
		item.lore("&3Arenas: &e" + ArenaManager.getAllEnabled(mechanic).size());
		item.lore("");
		item.lore("&fClick to view");
		return item;
	};

	Map<Integer, Integer> groupCountSlotIntervals = Map.of(
		2, 5,
		3, 3
	);

	@Override
	public String getTitle() {
		return camelCase(group);
	}

	@Override
	public void init() {
		final int count = group.getMechanics().size();

		if (!groupCountSlotIntervals.containsKey(count))
			throw new InvalidInputException("Unsupported group size " + count + " for group " + camelCase(group));

		final Function<MechanicType, Consumer<ItemClickData>> onClick = mechanic -> e -> new ArenasMenu(mechanic).open(viewer);

		int slot = 0;
		for (MechanicType mechanic : group.getMechanics()) {
			final ClickableItem image = ClickableItem.of(itemBuilder.apply(mechanic, mechanic.get().getMenuImage()), onClick.apply(mechanic));
			final ClickableItem filler = ClickableItem.of(itemBuilder.apply(mechanic, new ItemBuilder(CustomMaterial.INVISIBLE)), onClick.apply(mechanic));
			contents.fill(0, slot, 6, slot + (9 / count) - 1, filler);
			contents.set(0, slot, image);
			slot += groupCountSlotIntervals.get(count);
		}

	}

}
