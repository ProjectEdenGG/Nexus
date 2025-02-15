package gg.projecteden.nexus.features.events.y2025.pugmas25.balloons;

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

@Rows(3)
public class Pugmas25BalloonEditorMenu extends InventoryProvider {
	private static final ItemBuilder PASTE_SCHEM = new ItemBuilder(ItemModelType.GUI_ROTATE_LEFT).name("Reset").dyeColor(ColorType.LIGHT_RED).itemFlags(ItemFlags.HIDE_ALL);
	private static final ItemBuilder TEMPLATE_NEXT = new ItemBuilder(ItemModelType.GUI_ARROW_RIGHT).name("Next Template").dyeColor(ColorType.CYAN).itemFlags(ItemFlags.HIDE_ALL);
	private static final ItemBuilder TEMPLATE_PREVIOUS = new ItemBuilder(ItemModelType.GUI_ARROW_LEFT).name("Previous Template").dyeColor(ColorType.CYAN).itemFlags(ItemFlags.HIDE_ALL);
	private static final ItemBuilder EDITOR_EXIT = new ItemBuilder(ItemModelType.GUI_TRASHCAN).name("Exit").lore("&c(Doesn't save your progress)").dyeColor(ColorType.RED).itemFlags(ItemFlags.HIDE_ALL);
	private static final ItemBuilder EDITOR_SAVE = new ItemBuilder(ItemModelType.GUI_CHECK).name("Save").dyeColor(ColorType.LIGHT_GREEN).itemFlags(ItemFlags.HIDE_ALL);
	private static final String cooldownKeyButton = "pugmas25_balloon_editor-template_paste";

	private static final int row = 1;

	@Override
	public String getTitle() {
		return "Chosen Template: #" + Pugmas25BalloonEditor.schemId;
	}

	@Override
	public void init() {
		addCloseItem();

		contents.set(SlotPos.of(row, 1), ClickableItem.of(PASTE_SCHEM, e -> {
			if (!new CooldownService().check(Pugmas25BalloonEditor.getEditor().getUuid(), cooldownKeyButton, TickTime.SECOND)) {
				Pugmas25BalloonEditorUtils.send("&cSlow down");
				return;
			}

			Pugmas25BalloonEditorUtils.send("Reset Balloon");
			if (Pugmas25BalloonEditor.hasSchematic())
				Pugmas25BalloonEditor.pasteBalloon(Pugmas25BalloonEditor.getSchematicPath());
			else
				Pugmas25BalloonEditor.resetBalloon();

			close();
		}));

		contents.set(SlotPos.of(row, 2), ClickableItem.of(TEMPLATE_PREVIOUS, e -> {
			if (!new CooldownService().check(Pugmas25BalloonEditor.getEditor().getUuid(), cooldownKeyButton, TickTime.SECOND)) {
				Pugmas25BalloonEditorUtils.send("&cSlow down");
				return;
			}

			Pugmas25BalloonEditorUtils.send("Pasted previous template");
			Pugmas25BalloonEditorUtils.previousTemplate();
			refresh();
		}));

		contents.set(SlotPos.of(row, 3), ClickableItem.of(TEMPLATE_NEXT, e -> {
			if (!new CooldownService().check(Pugmas25BalloonEditor.getEditor().getUuid(), cooldownKeyButton, TickTime.SECOND)) {
				Pugmas25BalloonEditorUtils.send("&cSlow down");
				return;
			}

			Pugmas25BalloonEditorUtils.send("Pasted next template");
			Pugmas25BalloonEditorUtils.nextTemplate();
			refresh();
		}));

		contents.set(SlotPos.of(row, 5), ClickableItem.of(EDITOR_EXIT, e -> {
			Pugmas25BalloonEditorUtils.send("Exited without saving");
			Pugmas25BalloonEditor.reset();
			close();
		}));

		contents.set(SlotPos.of(row, 7), ClickableItem.of(EDITOR_SAVE, e -> {
			Pugmas25BalloonEditorUtils.send("Saving balloon...");
			Pugmas25BalloonEditor.saveBalloon();
			close();
		}));
	}
}
