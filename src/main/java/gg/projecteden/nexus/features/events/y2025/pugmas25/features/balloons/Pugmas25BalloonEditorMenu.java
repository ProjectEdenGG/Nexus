package gg.projecteden.nexus.features.events.y2025.pugmas25.features.balloons;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.api.content.SlotPos;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.ItemFlags;

@Rows(5)
public class Pugmas25BalloonEditorMenu extends InventoryProvider {
	private static final ItemBuilder PASTE_SCHEM = new ItemBuilder(ItemModelType.GUI_ROTATE_LEFT).name("&6Reset balloon").dyeColor(ColorType.ORANGE).itemFlags(ItemFlags.HIDE_ALL);
	private static final ItemBuilder TEMPLATE_NEXT = new ItemBuilder(ItemModelType.GUI_ARROW_RIGHT).name("&3Next Template").dyeColor(ColorType.CYAN).itemFlags(ItemFlags.HIDE_ALL);
	private static final ItemBuilder TEMPLATE_PREVIOUS = new ItemBuilder(ItemModelType.GUI_ARROW_LEFT).name("&3Previous Template").dyeColor(ColorType.CYAN).itemFlags(ItemFlags.HIDE_ALL);
	private static final ItemBuilder TEMPLATE_PLACE = new ItemBuilder(ItemModelType.GUI_CHECK).name("&aPlace Template").dyeColor(ColorType.LIGHT_GREEN).itemFlags(ItemFlags.HIDE_ALL);
	private static final ItemBuilder EDITOR_EXIT = new ItemBuilder(ItemModelType.GUI_TRASHCAN).name("&cExit").lore("&c&oWill not save").dyeColor(ColorType.GRAY).itemFlags(ItemFlags.HIDE_ALL);
	private static final ItemBuilder EDITOR_SAVE = new ItemBuilder(ItemModelType.GUI_SAVE).name("&aSave balloon").itemFlags(ItemFlags.HIDE_ALL);
	private static final String COOLDOWN_KEY = "pugmas25_balloon_editor-template_paste";
	private static final ItemModelType BASE_MODEL = ItemModelType.ITEM_DISPLAY_AIR_BALLOON_1;

	@Override
	public String getTitle() {
		return blankTexture() + "&8Template: #" + Pugmas25BalloonEditor.schemId;
	}

	@Override
	public void init() {
		addCloseItem();

		contents.set(SlotPos.of(1, 7), ClickableItem.of(EDITOR_SAVE, e -> {
			Pugmas25BalloonEditor.saveBalloon();
			Pugmas25BalloonEditorUtils.send("Saved balloon");
			close();
		}));

		contents.set(SlotPos.of(2, 7), ClickableItem.of(PASTE_SCHEM, e -> {
			if (CooldownService.isOnCooldown(Pugmas25BalloonEditor.getEditor().getUuid(), COOLDOWN_KEY, TickTime.SECOND)) {
				Pugmas25BalloonEditorUtils.send("&cSlow down");
				return;
			}

			Pugmas25BalloonEditorUtils.send("Reset");
			if (Pugmas25BalloonEditor.hasSchematic())
				Pugmas25BalloonEditor.pasteBalloon(Pugmas25BalloonEditor.getSchematicPath());
			else
				Pugmas25BalloonEditor.resetBalloon();

			close();
		}));

		contents.set(SlotPos.of(3, 7), ClickableItem.of(EDITOR_EXIT, e -> {
			Pugmas25BalloonEditorUtils.send("Exited without saving");
			Pugmas25BalloonEditor.reset();
			close();
		}));

		contents.set(SlotPos.of(3, 3), ClickableItem.of(TEMPLATE_PLACE, e -> {
			if (CooldownService.isOnCooldown(Pugmas25BalloonEditor.getEditor().getUuid(), COOLDOWN_KEY, TickTime.SECOND)) {
				Pugmas25BalloonEditorUtils.send("&cSlow down");
				return;
			}

			Pugmas25BalloonEditorUtils.send("Pasted selected template");
			Pugmas25BalloonEditor.pasteBalloon(Pugmas25BalloonManager.SCHEM_TEMPLATE + Pugmas25BalloonEditor.schemId);
			close();
		}));

		contents.set(SlotPos.of(3, 2), ClickableItem.of(TEMPLATE_PREVIOUS, e -> {
			Pugmas25BalloonEditorUtils.previousTemplate();
			refresh();
		}));

		contents.set(SlotPos.of(3, 4), ClickableItem.of(TEMPLATE_NEXT, e -> {
			Pugmas25BalloonEditorUtils.nextTemplate();
			refresh();
		}));

		String selectedModel = BASE_MODEL.name().replaceFirst("_\\d+$", "_" + Pugmas25BalloonEditor.schemId);
		ItemModelType displayModel = ItemModelType.valueOf(selectedModel);
		ItemBuilder displayItem = new ItemBuilder(displayModel).name("").itemFlags(ItemFlags.HIDE_ALL);
		contents.set(SlotPos.of(1, 3), ClickableItem.empty(displayItem));
	}
}
