package gg.projecteden.nexus.features.events.y2025.pugmas25.advent;

import gg.projecteden.api.common.utils.EnumUtils;
import gg.projecteden.api.common.utils.EnumUtils.IterableEnum;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.api.content.SlotPos;
import gg.projecteden.nexus.features.resourcepack.models.font.InventoryTexture;
import gg.projecteden.nexus.models.pugmas25.Advent25Config;
import gg.projecteden.nexus.models.pugmas25.Advent25User;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Tasks;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.time.DayOfWeek;

public class Pugmas25AdventMenu extends InventoryProvider {

	@NonNull
	private final Advent25User user;
	private final int frameTicks;
	private Title title = Title.FRAME_1;

	public Pugmas25AdventMenu(@NonNull Advent25User user, int frameTicks) {
		this.user = user;
		this.frameTicks = frameTicks;
	}

	@Override
	public String getTitle() {
		return title.getTitle();
	}

	@AllArgsConstructor
	public enum Title implements IterableEnum {
		FRAME_1(InventoryTexture.GUI_PUGMAS25_ADVENT_1),
		FRAME_2(InventoryTexture.GUI_PUGMAS25_ADVENT_2),
		;

		private final InventoryTexture character;

		public String getTitle() {
			return character.getMenuTexture(6);
		}
	}

	@Override
	public void init() {
		var row = 1;
		var column = EnumUtils.nextWithLoop(DayOfWeek.class, Pugmas25.get().getStart().getDayOfWeek().getValue()).ordinal();
		var slotIterator = MenuUtils.innerSlotIterator(contents, SlotPos.of(row, column));

		for (int day = 1; day <= 25; day++) {
			var present = Advent25Config.get().get(day);
			var item = new ItemBuilder(user.getStatus(present).getMenuItem(present));

			ClickableItem clickableItem = ClickableItem.empty(item.build());
			if (user.hasFound(present)) {
				item.lore("", "&aShow Waypoint");

				clickableItem = ClickableItem.of(item.build(), e -> {
					viewer.closeInventory();
					present.glow(user);
				});
			}

			slotIterator.next().set(clickableItem);
		}

		Tasks.wait(frameTicks, () -> {
			if (!isOpen())
				return;

			title = title.nextWithLoop();
			open(viewer, contents.pagination().getPage());
		});
	}


}
