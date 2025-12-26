package gg.projecteden.nexus.features.events.y2025.pugmas25.features;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteringRegionEvent;
import gg.projecteden.nexus.utils.PlayerUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;

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

	@EventHandler
	public void on(VehicleDamageEvent event) {
		if (event.getVehicle() instanceof Boat)
			return;

		if (!(event.getAttacker() instanceof Player player))
			return;

		if (!Pugmas25.get().isAtEvent(player))
			return;

		if (PlayerUtils.isWGEdit(player))
			return;

		event.setCancelled(true);
	}

	@EventHandler
	public void on(VehicleExitEvent event) {
		if (!(event.getVehicle() instanceof Boat boat))
			return;

		if (!(event.getExited() instanceof Player player))
			return;

		if (!Pugmas25.get().isAtEvent(player))
			return;

		if (boat.getPassengers().size() > 1)
			return;

		Material type = boat.getBoatMaterial();
		boat.remove();
		PlayerUtils.giveItem(player, type);
	}
}
