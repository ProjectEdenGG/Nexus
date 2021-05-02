package me.pugabyte.nexus.features.events.y2020.pugmas20.menu.providers;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.SlotPos;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.features.events.y2020.pugmas20.AdventChests;
import me.pugabyte.nexus.features.events.y2020.pugmas20.Pugmas20;
import me.pugabyte.nexus.features.events.y2020.pugmas20.menu.AdventMenu;
import me.pugabyte.nexus.features.events.y2020.pugmas20.models.AdventChest;
import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.models.pugmas20.Pugmas20User;
import me.pugabyte.nexus.models.pugmas20.Pugmas20UserService;
import me.pugabyte.nexus.utils.ItemBuilder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static me.pugabyte.nexus.features.events.y2020.pugmas20.Pugmas20.isSecondChance;

@NoArgsConstructor
@RequiredArgsConstructor
public class AdventProvider extends MenuUtils implements InventoryProvider {
	private static final ItemBuilder locked = AdventMenu.lockedHead.clone();
	private static final ItemBuilder missed = AdventMenu.missedHead.clone();
	private static final ItemBuilder toFind = AdventMenu.toFindHead.clone();

	@NonNull
	private LocalDate date;
	private final Pugmas20UserService service = new Pugmas20UserService();
	private Pugmas20User user;
	private AdventChest adventChest;
	private boolean located;

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
				String name = "&6Day: " + dayIndex;

				adventChest = AdventChests.getAdventChest(dayIndex);

				String districtName = "null";
				if (adventChest != null)
					districtName = adventChest.getDistrict().getName();

				String district = "&7District: &e" + districtName;

				located = user.getLocatedDays().contains(dayIndex);

				if (user.getFoundDays().contains(dayIndex))
					found(contents, slotPos, adventChest, skull, name, district, located);
				else {
					if (isSecondChance(date)) {
						if (dayIndex != 25 || user.getFoundDays().size() == 24)
							find(contents, slotPos, adventChest, name, district, located);
						else
							locked25(contents, slotPos, adventChest, name, located);
					} else {
						if (dayIndex == day)
							find(contents, slotPos, adventChest, name, district, located);
						else if (dayIndex < day)
							missed(contents, slotPos, adventChest, name, located);
						else
							locked(contents, slotPos, adventChest, name, district, located);
					}
				}
			}

			index.incrementAndGet();
		});
	}

	private void found(InventoryContents contents, SlotPos slotPos, AdventChest adventChest, ItemBuilder skull, String name, String district, boolean located) {
		if (located)
			contents.set(slotPos, ClickableItem.from(skull.clone().name(name).lore(showWaypoint(district)).build(),
					e -> Pugmas20.showWaypoint(adventChest, user.getOnlinePlayer())));
		else
			contents.set(slotPos, ClickableItem.empty(skull.clone().name(name).lore(district).build()));
	}

	private void find(InventoryContents contents, SlotPos slotPos, AdventChest adventChest, String name, String district, boolean located) {
		if (located)
			contents.set(slotPos, ClickableItem.from(toFind.clone().name(name).lore(showWaypoint("&aFind me!", district)).build(),
					e -> Pugmas20.showWaypoint(adventChest, user.getOnlinePlayer())));
		else
			contents.set(slotPos, ClickableItem.empty(toFind.clone().name(name).lore("&aFind me!", district).build()));
	}

	private void locked(InventoryContents contents, SlotPos slotPos, AdventChest adventChest, String name, String district, boolean located) {
		if (located)
			contents.set(slotPos, ClickableItem.from(locked.clone().name(name).lore(showWaypoint("&7Locked", district)).build(),
					e -> Pugmas20.showWaypoint(adventChest, user.getOnlinePlayer())));
		else
			contents.set(slotPos, ClickableItem.empty(locked.clone().name(name).lore("&7Locked").build()));
	}

	private void locked25(InventoryContents contents, SlotPos slotPos, AdventChest adventChest, String name, boolean located) {
		if (located)
			contents.set(slotPos, ClickableItem.from(locked.clone().name(name).lore(showWaypoint("&7Locked", "", "&cOpen all previous||&cchests to unlock")).build(),
					e -> Pugmas20.showWaypoint(adventChest, user.getOnlinePlayer())));
		else
			contents.set(slotPos, ClickableItem.empty(locked.clone().name(name).lore("&7Locked", "", "&cOpen all previous||&cchests to unlock").build()));
	}

	private void missed(InventoryContents contents, SlotPos slotPos, AdventChest adventChest, String name, boolean located) {
		if (located)
			contents.set(slotPos, ClickableItem.from(missed.clone().name(name).lore(showWaypoint("&cMissed")).build(),
					e -> Pugmas20.showWaypoint(adventChest, user.getOnlinePlayer())));
		else
			contents.set(slotPos, ClickableItem.empty(missed.clone().name(name).lore("&cMissed").build()));
	}

	@NotNull
	private List<String> showWaypoint(String... lines) {
		return new ArrayList<String>(Arrays.asList(lines)) {{
			add("&f||&aClick to show waypoint");
		}};
	}
}
