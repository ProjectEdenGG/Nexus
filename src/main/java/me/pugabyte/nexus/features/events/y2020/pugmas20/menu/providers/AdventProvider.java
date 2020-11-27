package me.pugabyte.nexus.features.events.y2020.pugmas20.menu.providers;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.SlotPos;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.events.y2020.pugmas20.AdventChests;
import me.pugabyte.nexus.features.events.y2020.pugmas20.menu.AdventMenu;
import me.pugabyte.nexus.features.events.y2020.pugmas20.models.AdventChest;
import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.models.pugmas20.Pugmas20Service;
import me.pugabyte.nexus.models.pugmas20.Pugmas20User;
import me.pugabyte.nexus.utils.ItemBuilder;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

import static me.pugabyte.nexus.features.events.y2020.pugmas20.Pugmas20.isSecondChance;

@NoArgsConstructor
@AllArgsConstructor
public class AdventProvider extends MenuUtils implements InventoryProvider {
	LocalDateTime date;
	private static final ItemBuilder locked = AdventMenu.lockedHead.clone();
	private static final ItemBuilder missed = AdventMenu.missedHead.clone();
	private static final ItemBuilder toFind = AdventMenu.toFindHead.clone();
	private static final Pugmas20Service service = new Pugmas20Service();
	private static Pugmas20User user;

	@Override
	public void init(Player player, InventoryContents contents) {
		addCloseItem(contents);

		user = service.get(player);
		int day = date.getDayOfMonth();

		AtomicInteger index = new AtomicInteger(1);
		AdventMenu.getAdventHeadMap().forEach((slotPos, skull) -> {
			if (index.get() >= 1 && index.get() <= 7) {
				// Days of the week
				contents.set(slotPos, ClickableItem.empty(skull.build()));
			} else {
				// Advent Days
				int dayIndex = (index.get() - 7);
				String name = "Day: " + dayIndex;

				AdventChest adventChest = AdventChests.getAdventChest(dayIndex);

				String districtName = "null";
				if (adventChest != null)
					districtName = adventChest.getDistrict().getName();

				String district = "District: " + districtName;

				if (user.getFoundDays().contains(dayIndex))
					found(contents, slotPos, skull, name, district);
				else {
					if (isSecondChance(date)) {
						if (dayIndex != 25 || user.getFoundDays().size() == 24)
							find(contents, slotPos, name, district);
						else
							locked25(contents, slotPos, name);
					} else {
						if (dayIndex == day)
							find(contents, slotPos, name, district);
						else if (dayIndex < day)
							missed(contents, slotPos, name);
						else
							locked(contents, slotPos, name);
					}
				}
			}

			index.incrementAndGet();
		});
	}

	private void found(InventoryContents contents, SlotPos slotPos, ItemBuilder skull, String name, String district) {
		contents.set(slotPos, ClickableItem.empty(skull.clone().name(name).lore(district).build()));
	}

	private void find(InventoryContents contents, SlotPos slotPos, String name, String district) {
		contents.set(slotPos, ClickableItem.empty(toFind.clone().name(name).lore("&aFind me!", district).build()));
	}

	private void locked(InventoryContents contents, SlotPos slotPos, String name) {
		contents.set(slotPos, ClickableItem.empty(locked.clone().name(name).lore("&7Locked").build()));
	}

	private void locked25(InventoryContents contents, SlotPos slotPos, String name) {
		contents.set(slotPos, ClickableItem.empty(locked.clone().name(name).lore("&7Locked", "", "&cOpen all previous||&cchests to unlock").build()));
	}

	private void missed(InventoryContents contents, SlotPos slotPos, String name) {
		contents.set(slotPos, ClickableItem.empty(missed.clone().name(name).lore("&cMissed").build()));
	}

	@Override
	public void update(Player player, InventoryContents contents) {
	}
}
