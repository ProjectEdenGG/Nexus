package gg.projecteden.nexus.features.store.perks.inventory.autoinventory.features;

import gg.projecteden.nexus.features.recipes.functionals.backpacks.Backpacks.BackpackMenu.BackpackHolder;
import gg.projecteden.nexus.features.store.perks.inventory.autoinventory.AutoInventoryFeature;
import gg.projecteden.nexus.features.store.perks.inventory.autoinventory.tasks.InventorySorter;
import gg.projecteden.nexus.models.autoinventory.AutoInventoryUser;
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

	public static void sort(AutoInventoryUser user, Inventory inventory) {
		if (!user.hasFeatureEnabled(AutoInventoryFeature.SORT_OWN_INVENTORY))
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
		AutoInventoryUser user = AutoInventoryUser.of(player);

		if (!user.hasFeatureEnabled(AutoInventoryFeature.SORT_OWN_INVENTORY))
			return;

		if (user.isSortingInventory())
			return;

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
		if (bottomInventory.getType() != InventoryType.PLAYER)
			return;

		HumanEntity holder = ((PlayerInventory) bottomInventory).getHolder();
		if (!(holder instanceof Player player))
			return;
		if (event.getView().getTopInventory().getHolder() instanceof BackpackHolder)
			return;

		AutoInventoryUser user = AutoInventoryUser.of(player);
		sort(user, bottomInventory);
	}

}
