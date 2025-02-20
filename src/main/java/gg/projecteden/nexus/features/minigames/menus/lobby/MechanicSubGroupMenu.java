package gg.projecteden.nexus.features.minigames.menus.lobby;

import gg.projecteden.api.common.utils.StringUtils;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.ItemClickData;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.minigames.lobby.MinigameInviter;
import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicSubGroup;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicType;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.features.resourcepack.models.font.InventoryTexture;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Tasks;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemFlag;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

@RequiredArgsConstructor
public class MechanicSubGroupMenu extends InventoryProvider {
	private final MechanicSubGroup group;

	private static final BiFunction<MechanicType, ItemBuilder, ItemBuilder> itemBuilder = (mechanic, item) -> {
		item.itemFlags(ItemFlag.values());
		item.name("&6&l" + mechanic.get().getName());
		item.lore("");
		item.lore("&3Arenas: &e" + ArenaManager.getAllEnabled(mechanic).size());
		item.lore("");
		item.lore("&fClick to view");
		return item;
	};

	@Override
	public String getTitle() {
		return InventoryTexture.MINIGAMES_MENU_SEPARATOR.getMenuTexture(getRows(null)) + "&8" + StringUtils.camelCase(group);
	}

	@Override
	public void init() {
		addCloseItemBottomInventory();

		final int count = group.getMechanics().size();

		final int slotInterval;
		final ItemModelType outlineItemModelType;

		switch (count) {
			case 2 -> {
				slotInterval = 5;
				outlineItemModelType = ItemModelType.GUI_OUTLINE_3x2;
			}
			case 3 -> {
				slotInterval = 3;
				outlineItemModelType = ItemModelType.GUI_OUTLINE_6x3;
			}
			default ->
				throw new InvalidInputException("Unsupported group size " + count + " for group " + StringUtils.camelCase(group));
		}

		final Function<MechanicType, Consumer<ItemClickData>> onClick = mechanic -> e -> {
			final boolean holdingInvite = ItemModelType.ENVELOPE_1.is(viewer.getItemOnCursor());
			new ArenasMenu(mechanic).open(viewer);
			if (holdingInvite) {
				if (MinigameInviter.canSendInvite(viewer))
					Tasks.wait(1, () -> viewer.setItemOnCursor(ArenasMenu.getInviteItem(viewer).build()));
			}
		};

		int slot = 0;
		for (MechanicType mechanic : group.getMechanics()) {
			final Function<ItemBuilder, ClickableItem> clickableItem = item ->
				ClickableItem.of(itemBuilder.apply(mechanic, item), onClick.apply(mechanic));

			final ClickableItem image = clickableItem.apply(mechanic.get().getMenuImage());
			final ClickableItem filler = clickableItem.apply(new ItemBuilder(ItemModelType.INVISIBLE));
			final ClickableItem outline = clickableItem.apply(new ItemBuilder(outlineItemModelType).dyeColor(ColorType.GRAY));

			contents.fill(0, slot, 6, slot + (9 / count) - 1, filler);
			contents.set(0, slot, image);
			contents.set(0, slot + 1, outline);
			slot += slotInterval;
		}

	}

}
