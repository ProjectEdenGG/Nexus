package gg.projecteden.nexus.features.minigames.lobby;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class Parkour implements Listener {

	public Parkour() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void onRegionEnter(PlayerEnteredRegionEvent event) {
		if (!event.getPlayer().getWorld().equals(Minigames.getWorld())) return;
		if (!"lobby_parkour_fall_1".equals(event.getRegion().getId())) return;

		event.getPlayer().teleportAsync(new Location(event.getPlayer().getWorld(), 1899.5, 33, 247.5, 270, 0));
	}

}
