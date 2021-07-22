package gg.projecteden.nexus.features.store.perks.autosort.features;

import gg.projecteden.nexus.features.store.perks.autosort.AutoSortFeature;
import gg.projecteden.nexus.features.store.perks.autosort.tasks.InventorySorter;
import gg.projecteden.nexus.models.autosort.AutoSortUser;
import gg.projecteden.nexus.models.tip.Tip.TipType;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NoArgsConstructor;
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
		if (!user.hasFeatureEnabled(AutoSortFeature.INVENTORY))
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

		if (!user.hasFeatureEnabled(AutoSortFeature.INVENTORY))
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
