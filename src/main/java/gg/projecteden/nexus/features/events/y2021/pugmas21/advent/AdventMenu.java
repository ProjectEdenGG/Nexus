package gg.projecteden.nexus.features.events.y2021.pugmas21.advent;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.SlotIterator;
import fr.minuskube.inv.content.SlotPos;
import gg.projecteden.nexus.features.events.y2021.pugmas21.Pugmas21;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.models.pugmas21.Pugmas21User;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.EnumUtils.IteratableEnum;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

import static gg.projecteden.nexus.utils.StringUtils.colorize;

@RequiredArgsConstructor
public class AdventMenu extends MenuUtils implements InventoryProvider {
	@NonNull
	private Pugmas21User user;
	@NonNull
	private LocalDate today;
	@NonNull
	private int frameTicks;
	private Title title = Title.FRAME_1;

	@Override
	public void open(Player player, int page) {
		SmartInventory.builder()
			.provider(this)
			.size(6, 9)
			.title(title.getTitle())
			.build()
			.open(player);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		int row = 1;
		int column = Pugmas21.EPOCH.getDayOfWeek().getValue() + 1;

		final SlotIterator slotIterator = innerSlotIterator(contents, SlotPos.of(row, column));
		for (int day = 1; day <= 25; day++) {
			final LocalDate date = Pugmas21.EPOCH.plusDays(day - 1);
			final Icon icon = getIcon(date);
			final ItemBuilder item = new ItemBuilder(icon.getItem(day));
			slotIterator.next().set(ClickableItem.empty(item.build()));
		}

		updateTask(player, contents);
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
			if (!isOpen(player))
				return;

			title = title.nextWithLoop();
			open(player, contents.pagination().getPage());
		});
	}

	@AllArgsConstructor
	public enum Title implements IteratableEnum {
		FRAME_1("ꈉ盆"),
		FRAME_2("ꈉ鉊"),
		;

		private String title;

		public String getTitle() {
			return colorize("&f" + title);
		}

	}

	@AllArgsConstructor
	public enum Icon {
		MISSED(Material.TRAPPED_CHEST, 3),
		OPENED(Material.TRAPPED_CHEST, 5),
		AVAILABLE(Material.TRAPPED_CHEST, 4),
		LOCKED(Material.WHITE_STAINED_GLASS_PANE, 1),
		;

		private final Material material;
		private final int customModelData;

		public ItemBuilder getItem(int day) {
			return new ItemBuilder(material)
				.customModelData(customModelData)
				.name(StringUtils.camelCase(name()));
		}
	}

}
