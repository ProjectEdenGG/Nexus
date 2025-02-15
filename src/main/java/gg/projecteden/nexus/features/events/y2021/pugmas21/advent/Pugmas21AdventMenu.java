package gg.projecteden.nexus.features.events.y2021.pugmas21.advent;

import gg.projecteden.api.common.utils.EnumUtils.IterableEnum;
import gg.projecteden.nexus.features.events.y2021.pugmas21.Pugmas21;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.api.content.SlotIterator;
import gg.projecteden.nexus.features.menus.api.content.SlotPos;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.features.resourcepack.models.font.CustomTexture;
import gg.projecteden.nexus.models.pugmas21.Advent21Config;
import gg.projecteden.nexus.models.pugmas21.Advent21Config.AdventPresent;
import gg.projecteden.nexus.models.pugmas21.Pugmas21User;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Tasks;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

@RequiredArgsConstructor
public class Pugmas21AdventMenu extends InventoryProvider {
	@NonNull
	private Pugmas21User user;
	@NonNull
	private LocalDate today;
	private int frameTicks;
	private Title title = Title.FRAME_1;

	public Pugmas21AdventMenu(@NonNull Pugmas21User user, @NonNull LocalDate today, int frameTicks) {
		this.user = user;
		this.today = today;
		this.frameTicks = frameTicks;
	}

	@Override
	public String getTitle() {
		return title.getTitle();
	}

	@Override
	public void init() {
		int row = 1;
		int column = Pugmas21.EPOCH.getDayOfWeek().getValue() + 1;

		final SlotIterator slotIterator = MenuUtils.innerSlotIterator(contents, SlotPos.of(row, column));
		for (int day = 1; day <= 25; day++) {
			final int _day = day;

			final LocalDate date = Pugmas21.EPOCH.plusDays(_day - 1);
			final Icon icon = getIcon(date);
			final ItemBuilder item = new ItemBuilder(icon.getItem(_day));

			ClickableItem clickableItem = ClickableItem.empty(item.build());
			if (user.advent().hasFound(_day)) {
				item.lore("", "&aShow Waypoint");

				clickableItem = ClickableItem.of(item.build(), e -> {
					viewer.closeInventory();
					Pugmas21Advent.glow(user, _day);
				});
			}

			slotIterator.next().set(clickableItem);
		}

		updateTask(viewer, contents);
	}

	@NotNull
	private Icon getIcon(LocalDate date) {
		final Icon icon;
		if (user.advent().hasCollected(date))
			icon = Icon.OPENED;
		else if (date.isAfter(today))
			icon = Icon.LOCKED;
		else if (date.equals(today) || Pugmas21.isPugmasOrAfter(today))
			icon = Icon.AVAILABLE;
		else
			icon = Icon.MISSED;

		return icon;
	}

	private void updateTask(Player player, InventoryContents contents) {
		Tasks.wait(frameTicks, () -> {
			if (!isOpen())
				return;

			title = title.nextWithLoop();
			open(player, contents.pagination().getPage());
		});
	}

	@AllArgsConstructor
	public enum Title implements IterableEnum {
		FRAME_1(CustomTexture.GUI_PUGMAS21_ADVENT_1),
		FRAME_2(CustomTexture.GUI_PUGMAS21_ADVENT_2),
		;

		private final CustomTexture character;

		public String getTitle() {
			return character.getMenuTexture(6);
		}

	}

	@AllArgsConstructor
	public enum Icon {
		MISSED(ItemModelType.PUGMAS_PRESENT_OUTLINED, "&cMissed"),
		OPENED(ItemModelType.PUGMAS_PRESENT_OPENED, "&aOpened"),
		AVAILABLE(ItemModelType.PUGMAS_PRESENT_COLORED, "&a&oAvailable"),
		LOCKED(ItemModelType.PUGMAS_PRESENT_LOCKED, "&7Locked"),
		;

		private final ItemModelType itemModelType;
		private final String status;

		public ItemBuilder getItem(int day) {
			AdventPresent present = Advent21Config.get().get(day);

			return new ItemBuilder(itemModelType)
				.name("&3Day: &e" + present.getDay())
				.lore("&3Status: &e" + status)
				.lore("&3District: &e" + present.getDistrict().getName());
		}
	}

}
