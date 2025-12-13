package gg.projecteden.nexus.features.listeners;

import gg.projecteden.parchment.event.entity.PreHangingEntityBreakEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class HangingBreakPass implements Listener {

	@EventHandler
	public void on(PreHangingEntityBreakEvent event) {
		event.setCancelled(false);
	}

}
