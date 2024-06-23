package gg.projecteden.nexus.features.events.y2024.pugmas24.advent;

import gg.projecteden.api.common.utils.EnumUtils;
import gg.projecteden.api.common.utils.EnumUtils.IterableEnum;
import gg.projecteden.nexus.features.events.y2024.pugmas24.Pugmas24;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.api.content.SlotIterator;
import gg.projecteden.nexus.features.menus.api.content.SlotPos;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.resourcepack.models.font.CustomTexture;
import gg.projecteden.nexus.models.pugmas24.Advent24Config;
import gg.projecteden.nexus.models.pugmas24.Advent24Present;
import gg.projecteden.nexus.models.pugmas24.Pugmas24User;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Tasks;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.time.DayOfWeek;
import java.time.LocalDate;

import static gg.projecteden.nexus.features.menus.MenuUtils.innerSlotIterator;

public class Advent24Menu extends InventoryProvider {

	@NonNull
	private final Pugmas24User user;

	@NonNull
	private final LocalDate today;
	private final int frameTicks;
	private Title title = Title.FRAME_1;

	public Advent24Menu(@NonNull Pugmas24User user, @NonNull LocalDate today, int frameTicks) {
		this.user = user;
		this.today = today;
		this.frameTicks = frameTicks;
	}

	@Override
	public String getTitle() {
		return title.getTitle();
	}

	@AllArgsConstructor
	public enum Title implements IterableEnum {
		FRAME_1(CustomTexture.GUI_PUGMAS24_ADVENT_1),
		FRAME_2(CustomTexture.GUI_PUGMAS24_ADVENT_2),
		;

		private final CustomTexture character;

		public String getTitle() {
			return character.getMenuTexture(6);
		}

	}

	@Override
	public void init() {
		int row = 1;
		int column = EnumUtils.nextWithLoop(DayOfWeek.class, Pugmas24.get().getStart().getDayOfWeek().getValue()).getValue();

		final SlotIterator slotIterator = innerSlotIterator(contents, SlotPos.of(row, column));
		for (int dayNdx = 1; dayNdx <= 25; dayNdx++) {
			final LocalDate date = Pugmas24.get().getStart().plusDays(dayNdx - 1);
			final Icon icon = Icon.fromDate(user, today, date);
			final ItemBuilder item = new ItemBuilder(icon.getItem(dayNdx));

			slotIterator.next().set(ClickableItem.empty(item.build()));
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
		MISSED(CustomMaterial.PUGMAS_PRESENT_OUTLINED, "&cMissed"),
		OPENED(CustomMaterial.PUGMAS_PRESENT_OPENED, "&aOpened"),
		AVAILABLE(CustomMaterial.PUGMAS_PRESENT_COLORED, "&a&oAvailable"),
		LOCKED(CustomMaterial.PUGMAS_PRESENT_LOCKED, "&7Locked"),
		;

		private final CustomMaterial material;
		private final String status;

		public ItemBuilder getItem(int day) {
			Advent24Present present = Advent24Config.get().get(day);

			return new ItemBuilder(material)
					.name("&3Day: &e" + present.getDay())
					.lore("&3Status: &e" + status)
					.lore("&3District: &e" + present.getDistrict().getName());
		}

		public static Icon fromDate(Pugmas24User user, LocalDate today, LocalDate date) {
			final Icon icon;
			if (user.advent().hasCollected(date))
				icon = Icon.OPENED;
			else if (date.isAfter(today))
				icon = Icon.LOCKED;
			else if (date.equals(today) || Pugmas24.is25thOrAfter(today))
				icon = Icon.AVAILABLE;
			else
				icon = Icon.MISSED;

			return icon;
		}
	}
}
