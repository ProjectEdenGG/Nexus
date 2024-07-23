package gg.projecteden.nexus.features.events.y2024.pugmas24.balloons;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.api.content.SlotPos;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.ItemFlags;

@Rows(3)
public class BalloonEditorMenu extends InventoryProvider {
	private static final ItemBuilder PASTE_SCHEM = new ItemBuilder(CustomMaterial.GUI_ROTATE_LEFT).name("Reset").dyeColor(ColorType.LIGHT_RED).itemFlags(ItemFlags.HIDE_ALL);
	private static final ItemBuilder TEMPLATE_NEXT = new ItemBuilder(CustomMaterial.GUI_ARROW_NEXT).name("Next Template").dyeColor(ColorType.CYAN).itemFlags(ItemFlags.HIDE_ALL);
	private static final ItemBuilder TEMPLATE_PREVIOUS = new ItemBuilder(CustomMaterial.GUI_ARROW_PREVIOUS).name("Previous Template").dyeColor(ColorType.CYAN).itemFlags(ItemFlags.HIDE_ALL);
	private static final ItemBuilder EDITOR_EXIT = new ItemBuilder(CustomMaterial.GUI_TRASHCAN).name("Exit").lore("&c(Doesn't save your progress)").dyeColor(ColorType.RED).itemFlags(ItemFlags.HIDE_ALL);
	private static final ItemBuilder EDITOR_SAVE = new ItemBuilder(CustomMaterial.GUI_CHECK).name("Save").dyeColor(ColorType.LIGHT_GREEN).itemFlags(ItemFlags.HIDE_ALL);
	private static final String cooldownKeyButton = "pugmas24_balloon_editor-template_paste";

	private static final int row = 1;

	@Override
	public String getTitle() {
		return "Chosen Template: #" + BalloonEditor.schemId;
	}

	@Override
	public void init() {
		addCloseItem();

		contents.set(SlotPos.of(row, 1), ClickableItem.of(PASTE_SCHEM, e -> {
			if (!new CooldownService().check(BalloonEditor.getEditor().getUuid(), cooldownKeyButton, TickTime.SECOND)) {
				BalloonEditorUtils.send("&cSlow down");
				return;
			}

			BalloonEditorUtils.send("Reset Balloon");
			if (BalloonEditor.hasSchematic())
				BalloonEditor.pasteBalloon(BalloonEditor.getSchematicPath());
			else
				BalloonEditor.resetBalloon();

			close();
		}));

		contents.set(SlotPos.of(row, 2), ClickableItem.of(TEMPLATE_PREVIOUS, e -> {
			if (!new CooldownService().check(BalloonEditor.getEditor().getUuid(), cooldownKeyButton, TickTime.SECOND)) {
				BalloonEditorUtils.send("&cSlow down");
				return;
			}

			BalloonEditorUtils.send("Pasted previous template");
			BalloonEditorUtils.previousTemplate();
			refresh();
		}));

		contents.set(SlotPos.of(row, 3), ClickableItem.of(TEMPLATE_NEXT, e -> {
			if (!new CooldownService().check(BalloonEditor.getEditor().getUuid(), cooldownKeyButton, TickTime.SECOND)) {
				BalloonEditorUtils.send("&cSlow down");
				return;
			}

			BalloonEditorUtils.send("Pasted next template");
			BalloonEditorUtils.nextTemplate();
			refresh();
		}));

		contents.set(SlotPos.of(row, 5), ClickableItem.of(EDITOR_EXIT, e -> {
			BalloonEditorUtils.send("Exited without saving");
			BalloonEditor.reset();
			close();
		}));

		contents.set(SlotPos.of(row, 7), ClickableItem.of(EDITOR_SAVE, e -> {
			BalloonEditorUtils.send("Saving balloon...");
			BalloonEditor.saveBalloon();
			close();
		}));
	}
}
