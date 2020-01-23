package me.pugabyte.bncore.features.clearinventory;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.clearinventory.models.ClearInventoryPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class ClearInventoryListener implements Listener {

	ClearInventoryListener() {
		BNCore.registerListener(this);
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		ClearInventoryPlayer ciPlayer = ClearInventory.getPlayer(event.getEntity());
		ciPlayer.removeCache();
	}
}
