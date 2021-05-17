package me.pugabyte.nexus.features.autosort.features;

import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.autosort.AutoSortFeature;
import me.pugabyte.nexus.models.autosort.AutoSortUser;
import me.pugabyte.nexus.models.tip.Tip.TipType;
import org.bukkit.GameMode;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;

@NoArgsConstructor
public class AutoDeposit implements Listener {

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onInventoryClose(InventoryCloseEvent event) {
		Inventory bottomInventory = event.getView().getBottomInventory();
		if (bottomInventory.getType() != InventoryType.PLAYER) return;

		HumanEntity holder = ((PlayerInventory) bottomInventory).getHolder();
		if (!(holder instanceof Player player)) return;

		AutoSortUser user = AutoSortUser.of(player);

		if (user.isFeatureEnabled(AutoSortFeature.DEPOSIT_ALL))
			if (player.getGameMode() != GameMode.CREATIVE)
				if (event.getView().getTopInventory().getType() == InventoryType.CHEST)
					user.tip(TipType.AUTOSORT_DEPOSIT_ALL);
	}

}
