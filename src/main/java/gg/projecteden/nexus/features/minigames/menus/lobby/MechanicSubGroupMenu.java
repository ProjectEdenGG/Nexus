package gg.projecteden.nexus.features.minigames.menus.lobby;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.ItemClickData;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicSubGroup;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicType;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.FontUtils;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.RequiredArgsConstructor;

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

	@Override
	public String getTitle() {
		return
				FontUtils.minus(10) +
				"&f敷"
				+ FontUtils.minus(114) +
				"&8" + camelCase(group);
	}

	@Override
	public void init() {
		final int count = group.getMechanics().size();

		final int slotInterval;
		final CustomMaterial outlineMaterial;

		switch (count) {
			case 2 -> {
				slotInterval = 5;
				outlineMaterial = CustomMaterial.GUI_OUTLINE_3x2;
			}
			case 3 -> {
				slotInterval = 3;
				outlineMaterial = CustomMaterial.GUI_OUTLINE_6x3;
			}
			default ->
				throw new InvalidInputException("Unsupported group size " + count + " for group " + camelCase(group));
		}

		final Function<MechanicType, Consumer<ItemClickData>> onClick = mechanic -> e -> new ArenasMenu(mechanic).open(viewer);

		int slot = 0;
		for (MechanicType mechanic : group.getMechanics()) {
			final Function<ItemBuilder, ClickableItem> clickableItem = itemBuilder ->
				ClickableItem.of(MechanicSubGroupMenu.itemBuilder.apply(mechanic, itemBuilder), onClick.apply(mechanic));

			final ClickableItem image = clickableItem.apply(mechanic.get().getMenuImage());
			final ClickableItem filler = clickableItem.apply(new ItemBuilder(CustomMaterial.INVISIBLE));
			final ClickableItem outline = clickableItem.apply(new ItemBuilder(outlineMaterial).dyeColor(ColorType.GRAY));

			contents.fill(0, slot, 6, slot + (9 / count) - 1, filler);
			contents.set(0, slot, image);
			contents.set(0, slot + 1, outline);
			slot += slotInterval;
		}

	}

}
