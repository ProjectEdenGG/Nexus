package me.pugabyte.bncore.features.clearinventory;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.clearinventory.models.ClearInventoryPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class ClearInventoryListener implements Listener {

	ClearInventoryListener() {
		BNCore.registerListener(this);
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		ClearInventoryPlayer ciPlayer = BNCore.clearInventory.getPlayer(player);
		ciPlayer.removeCache();
	}
}
