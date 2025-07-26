package gg.projecteden.nexus.features.events.y2025.pugmas25.models;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteringRegionEvent;
import org.bukkit.Location;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class Pugmas25BoatRace implements Listener {

	private static final String teleportRegion = "pugmas25_boat_tp";
	private static final Location teleportLocation = Pugmas25.get().location(-591.5, 165.5, -3257.5, -103.75F, 1.35F);

	public Pugmas25BoatRace() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void on(PlayerEnteringRegionEvent event) {
		Player player = event.getPlayer();
		if (!Pugmas25.get().isAtEvent(player))
			return;

		if (!event.getRegion().getId().equalsIgnoreCase(teleportRegion))
			return;

		if (!(player.getVehicle() instanceof Boat boat))
			return;

		boat.removePassenger(player);
		boat.teleport(teleportLocation);
		player.teleport(teleportLocation);
		boat.addPassenger(player);
	}
}
