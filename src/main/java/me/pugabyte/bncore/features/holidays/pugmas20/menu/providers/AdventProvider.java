package me.pugabyte.bncore.features.holidays.pugmas20.menu.providers;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import me.pugabyte.bncore.features.holidays.pugmas20.AdventChests;
import me.pugabyte.bncore.features.holidays.pugmas20.menu.AdventMenu;
import me.pugabyte.bncore.features.holidays.pugmas20.models.AdventChest;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.models.pugmas20.Pugmas20Service;
import me.pugabyte.bncore.models.pugmas20.Pugmas20User;
import me.pugabyte.bncore.utils.ItemBuilder;
import org.bukkit.entity.Player;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

@NoArgsConstructor
@AllArgsConstructor
public class AdventProvider extends MenuUtils implements InventoryProvider {
	int day;
	private static final ItemBuilder locked = AdventMenu.lockedHead.clone();
	private static final ItemBuilder missed = AdventMenu.missedHead.clone();
	private static final ItemBuilder toFind = AdventMenu.toFindHead.clone();
	private static final Pugmas20Service service = new Pugmas20Service();
	private static Pugmas20User user;

	@Override
	public void init(Player player, InventoryContents contents) {
		user = service.get(player);

		addCloseItem(contents, 5, 1);
		if (day == -1)
			day = LocalDate.now().getDayOfMonth();

		AtomicInteger index = new AtomicInteger(1);
		AdventMenu.getAdventHeadMap().forEach((slotPos, skull) -> {
			// Days of the week
			if (index.get() >= 1 && index.get() <= 7) {
				contents.set(slotPos, ClickableItem.empty(skull.build()));

				// Presents
			} else {
				int dayIndex = (index.get() - 7);
				String name = "Day: " + dayIndex;
				AdventChest adventChest = AdventChests.getAdventChest(dayIndex);
				String district = "District: N/A";
				if (adventChest != null)
					district = "District: " + adventChest.getDistrict();

				if (user.getFoundDays().contains(dayIndex)) {
					// Found
					contents.set(slotPos, ClickableItem.empty(skull.clone().name(name).lore(district).build()));
				} else {
					if (dayIndex == day) {
						// Find Me
						contents.set(slotPos, ClickableItem.empty(toFind.clone().name(name).lore("&aFind me!", district).build()));
					} else if (dayIndex < day) {
						// Missed
						contents.set(slotPos, ClickableItem.empty(missed.clone().name(name).lore("&cMissed").build()));
					} else {
						// Locked
						contents.set(slotPos, ClickableItem.empty(locked.clone().name(name).lore("&7Locked").build()));
					}
				}
			}

			index.incrementAndGet();
		});
	}

	@Override
	public void update(Player player, InventoryContents contents) {
	}
}
