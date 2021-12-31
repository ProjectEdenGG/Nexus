package gg.projecteden.nexus.features.store.perks.autosort.features;

import gg.projecteden.nexus.features.store.perks.autosort.AutoInventory;
import gg.projecteden.nexus.features.store.perks.autosort.AutoInventoryFeature;
import gg.projecteden.nexus.features.store.perks.autosort.tasks.InventorySorter;
import gg.projecteden.nexus.models.autosort.AutoInventoryUser;
import gg.projecteden.nexus.models.tip.Tip.TipType;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NoArgsConstructor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;

import static gg.projecteden.nexus.utils.PlayerUtils.isVanished;

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

		AutoInventoryUser user = AutoInventoryUser.of(player);

		if (!user.hasFeatureEnabled(AutoInventoryFeature.SORT_OTHER_INVENTORIES))
			return;

		if (player.isSneaking() || isVanished(player))
			return;

		Inventory topInventory = event.getView().getTopInventory();
		if (!AutoInventory.isSortableChestInventory(player, topInventory, event.getView().getTitle()))
			return;

		Tasks.wait(1, new InventorySorter(topInventory, 0));
		user.tip(TipType.AUTOSORT_SORT_CHESTS);
	}

}
