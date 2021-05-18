package me.pugabyte.nexus.features.store.perks.autosort.features;

import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.store.perks.autosort.AutoSortFeature;
import me.pugabyte.nexus.features.store.perks.autosort.tasks.InventorySorter;
import me.pugabyte.nexus.models.autosort.AutoSortUser;
import me.pugabyte.nexus.models.tip.Tip.TipType;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;

@NoArgsConstructor
public class AutoSortInventory implements Listener {

	public static void sort(AutoSortUser user, Inventory inventory) {
		if (!user.hasFeatureEnabled(AutoSortFeature.SORT_INVENTORY))
			return;

		Player player = user.getOnlinePlayer();
		// Dont sort inventory if any other inventory is open
		if (player.getOpenInventory().getTopInventory().getType() != InventoryType.CRAFTING)
			return;

		new InventorySorter(inventory, 9).run();

		user.tip(TipType.AUTOSORT_SORT_INVENTORY);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onPickupItem(EntityPickupItemEvent event) {
		if (!(event.getEntity() instanceof Player player))
			return;
		AutoSortUser user = AutoSortUser.of(player);

		if (!user.hasFeatureEnabled(AutoSortFeature.SORT_INVENTORY))
			return;

		if (user.isSortingInventory()) return;
		user.setSortingInventory(true);

		final int firstEmpty = user.getInventory().firstEmpty();
		if (firstEmpty >= 9)
			Tasks.wait(10, () -> {
				if (firstEmpty != user.getInventory().firstEmpty())
					sort(user, user.getInventory());
				user.setSortingInventory(false);
			});
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onInventoryOpen(InventoryOpenEvent event) {
		Inventory bottomInventory = event.getView().getBottomInventory();
		if (bottomInventory.getType() != InventoryType.PLAYER) return;

		HumanEntity holder = ((PlayerInventory) bottomInventory).getHolder();
		if (!(holder instanceof Player player)) return;

		AutoSortUser user = AutoSortUser.of(player);
		sort(user, bottomInventory);
	}

}
