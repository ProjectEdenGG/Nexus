package gg.projecteden.nexus.features.workbenches.dyestation;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.SlotPos;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.workbenches.dyestation.ColorChoice.ChoiceType;
import gg.projecteden.nexus.features.workbenches.dyestation.ColorChoice.ColoredButton;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.Color;
import org.bukkit.inventory.ItemFlag;

import java.util.List;

public interface IDyeMenu {
	SlotPos SLOT_COSTUME = new SlotPos(2, 1);

	SlotPos SLOT_CHEAT_DYE = new SlotPos(0, 3);
	SlotPos SLOT_CHEAT_STAIN = new SlotPos(0, 5);

	SlotPos SLOT_STAIN_PREVIOUS = new SlotPos(5, 1);
	SlotPos SLOT_STAIN_NEXT = new SlotPos(5, 7);

	SlotPos SLOT_INPUT = new SlotPos(1, 1);
	SlotPos SLOT_DYE = new SlotPos(3, 1);
	SlotPos SLOT_RESULT = new SlotPos(2, 7);

	ItemBuilder STAIN_NEXT = new ItemBuilder(CustomMaterial.GUI_ARROW_NEXT)
			.dyeColor(ColorType.CYAN)
			.itemFlags(ItemFlag.HIDE_DYE)
			.name("Next Page");

	ItemBuilder STAIN_PREVIOUS = new ItemBuilder(CustomMaterial.GUI_ARROW_PREVIOUS)
			.dyeColor(ColorType.CYAN)
			.itemFlags(ItemFlag.HIDE_DYE)
			.name("Previous Page");

	default void emptyColorOptions(InventoryContents contents) {
		// main colors
		for (int row = 1; row < 4; row++) {
			for (int col = 3; col < 6; col++) {
				contents.set(SlotPos.of(row, col), ClickableItem.AIR);
			}
		}

		// color choices
		for (int i = 1; i < 8; i++) {
			contents.set(SlotPos.of(5, i), ClickableItem.AIR);
		}
	}

	default void fillColors(InventoryContents contents, ColorChoice.ChoiceType dyeType, int colorPage) {
		int row = 1;
		int col = 3;
		int index = 0;
		int countAdded = 0;


		switch (dyeType) {
			case DYE -> {
				for (ColorChoice.DyeChoice dyeChoice : ColorChoice.DyeChoice.values()) {
					String itemName = StringUtils.camelCase(dyeChoice) + "s";
					contents.set(row, col++, ClickableItem.of(dyeChoice.getItem(itemName), e -> fillChoices(contents, dyeChoice, ChoiceType.DYE)));

					if (++index == 3) {
						++row;
						col = 3;
						index = 0;
					}
				}
			}

			case STAIN -> {
				int skipCount = colorPage * 9;

				for (ColorChoice.StainChoice stainChoice : ColorChoice.StainChoice.values()) {
					if (skipCount > index) {
						index++;
						continue;
					}

					if (countAdded >= 9) {
						continue;
					}

					String itemName = StringUtils.camelCase(stainChoice);
					Color color = stainChoice.getButton().getColor();
					contents.set(row, col++, ClickableItem.of(stainChoice.getItem(itemName), e -> setResultItem(color)));
					countAdded++;

					if (++index == 3) {
						++row;
						col = 3;
						index = 0;
					}
				}

				if (Math.ceil(ColorChoice.StainChoice.values().length / 9.0) > (colorPage + 1))
					contents.set(SLOT_STAIN_NEXT, ClickableItem.of(STAIN_NEXT, e -> reopenMenu(contents, colorPage + 1)));

				if (colorPage > 0)
					contents.set(SLOT_STAIN_PREVIOUS, ClickableItem.of(STAIN_PREVIOUS, e -> reopenMenu(contents, colorPage - 1)));
			}
		}
	}

	default void fillChoices(InventoryContents contents, ColorChoice.DyeChoice dyeChoice, ColorChoice.ChoiceType choiceType) {
		int col = 1;
		List<ColoredButton> choices = dyeChoice.getChoices();
		for (int i = 0; i < 7; i++) {
			if (i > choices.size() - 1)
				break;

			ColorChoice.ColoredButton button = choices.get(i);
			contents.set(5, col, ClickableItem.of(button.getItem(choiceType, "Select Shade"), e -> setResultItem(button.getColor())));
			++col;
		}
	}

	default void setResultItem(Color color) {
	}

	default void reopenMenu(InventoryContents contents) {
	}

	default void reopenMenu(InventoryContents contents, int dyePage) {
	}


}
