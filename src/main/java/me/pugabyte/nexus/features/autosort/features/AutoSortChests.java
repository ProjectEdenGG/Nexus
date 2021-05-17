package me.pugabyte.nexus.features.autosort.features;

import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.autosort.AutoSort;
import me.pugabyte.nexus.features.autosort.AutoSortFeature;
import me.pugabyte.nexus.features.autosort.tasks.InventorySorter;
import me.pugabyte.nexus.models.autosort.AutoSortUser;
import me.pugabyte.nexus.models.tip.Tip.TipType;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;

import static me.pugabyte.nexus.utils.PlayerUtils.isVanished;

@NoArgsConstructor
public class AutoSortChests implements Listener {

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onInventoryOpen(InventoryOpenEvent event) {
		Inventory bottomInventory = event.getView().getBottomInventory();
		if (bottomInventory.getType() != InventoryType.PLAYER)
			return;

		HumanEntity holder = ((PlayerInventory) bottomInventory).getHolder();
		if (!(holder instanceof Player player))
			return;

		AutoSortUser user = AutoSortUser.of(player);

		if (!user.isFeatureEnabled(AutoSortFeature.SORT_CHESTS))
			return;

		if (player.isSneaking() || isVanished(player))
			return;

		Inventory topInventory = event.getView().getTopInventory();
		if (!AutoSort.isSortableChestInventory(topInventory, event.getView().getTitle()))
			return;

		Tasks.wait(1, new InventorySorter(topInventory, 0));
		user.tip(TipType.AUTOSORT_SORT_CHESTS);
	}

}
