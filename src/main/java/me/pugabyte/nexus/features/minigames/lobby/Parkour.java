package me.pugabyte.nexus.features.minigames.lobby;

import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.minigames.Minigames;
import me.pugabyte.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
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
		if (!event.getRegion().getId().equals("lobby_parkour_fall_1")) return;

		event.getPlayer().teleport(new Location(event.getPlayer().getWorld(), 1899.5, 33, 247.5, 270, 0));
	}


}
