package gg.projecteden.nexus.features.minigolf.listeners;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.BearFair21MiniGolf;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.CauldronLevelChangeEvent;
import org.bukkit.event.block.CauldronLevelChangeEvent.ChangeReason;

public class Listeners implements Listener {

	public Listeners() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void onCauldronLevelChange(CauldronLevelChangeEvent event) {
		Location location = event.getBlock().getLocation();
		if (!new WorldGuardUtils(location).isInRegionLikeAt(BearFair21MiniGolf.getRegionHole(), location))
			return;

		if (event.getReason().equals(ChangeReason.NATURAL_FILL))
			event.setCancelled(true);
	}
}
