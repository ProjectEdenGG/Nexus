package me.pugabyte.nexus.features.events.y2020.pugmas20.menu.providers;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.events.y2020.pugmas20.AdventChests;
import me.pugabyte.nexus.features.events.y2020.pugmas20.Pugmas20;
import me.pugabyte.nexus.features.events.y2020.pugmas20.menu.AdventMenu;
import me.pugabyte.nexus.features.events.y2020.pugmas20.models.AdventChest;
import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.models.pugmas20.Pugmas20Service;
import me.pugabyte.nexus.models.pugmas20.Pugmas20User;
import me.pugabyte.nexus.utils.ItemBuilder;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

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
			// Days of the week
			if (index.get() >= 1 && index.get() <= 7) {
				contents.set(slotPos, ClickableItem.empty(skull.build()));

				// Presents
			} else {
				int dayIndex = (index.get() - 7);
				String name = "Day: " + dayIndex;
				AdventChest adventChest = AdventChests.getAdventChest(dayIndex);
				String districtName = "null";
				if (adventChest != null)
					districtName = adventChest.getDistrict().getName();

				String district = "District: " + districtName;

				if (user.getFoundDays().contains(dayIndex)) {
					// Found
					contents.set(slotPos, ClickableItem.empty(skull.clone().name(name).lore(district).build()));

				} else {
					if (dayIndex == day || Pugmas20.isSecondChance(date)) {
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
