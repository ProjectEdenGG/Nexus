package gg.projecteden.nexus.features.events.y2025.pugmas25.advent;

import gg.projecteden.api.common.utils.EnumUtils;
import gg.projecteden.api.common.utils.EnumUtils.IterableEnum;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Districts.Pugmas25District;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.api.content.SlotPos;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.features.resourcepack.models.font.InventoryTexture;
import gg.projecteden.nexus.models.pugmas25.Advent25Config;
import gg.projecteden.nexus.models.pugmas25.Advent25Present;
import gg.projecteden.nexus.models.pugmas25.Pugmas25User;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Tasks;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.time.DayOfWeek;

public class Pugmas25AdventMenu extends InventoryProvider {

	@NonNull
	private final Pugmas25User user;
	private final int frameTicks;
	private Title title = Title.FRAME_1;

	public Pugmas25AdventMenu(@NonNull Pugmas25User user, int frameTicks) {
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
			var item = new ItemBuilder(Icon.of(user, present).getItem(present));

			ClickableItem clickableItem = ClickableItem.empty(item.build());
			if (user.advent().hasFound(present)) {
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

	@AllArgsConstructor
	public enum Icon {
		MISSED(ItemModelType.PUGMAS_PRESENT_OUTLINED, "&cMissed"),
		OPENED(ItemModelType.PUGMAS_PRESENT_OPENED, "&aOpened"),
		AVAILABLE(ItemModelType.PUGMAS_PRESENT_COLORED, "&6Available"),
		LOCKED(ItemModelType.PUGMAS_PRESENT_LOCKED, "&7Locked"),
		;

		private final ItemModelType itemModelType;
		private final String status;

		public ItemBuilder getItem(Advent25Present present) {
			Pugmas25District district = present.getDistrict();
			String districtName = "null";
			if (district != null) {
				districtName = district.getName();
				if (this == LOCKED)
					districtName = "???";
			}

			return new ItemBuilder(itemModelType)
				.name("&3Day: &e" + present.getDay())
				.lore("&3Status: &e" + status)
				.lore("&3District: &e" + districtName);
		}

		public static Icon of(Pugmas25User user, Advent25Present present) {
			final Icon icon;
			if (user.advent().hasCollected(present))
				icon = Icon.OPENED;
			else if (user.advent().canCollect(present))
				icon = Icon.AVAILABLE;
			else if (Pugmas25.get().now().isBefore(present.getDate().atStartOfDay()))
				icon = Icon.LOCKED;
			else
				icon = Icon.MISSED;

			return icon;
		}
	}
}
