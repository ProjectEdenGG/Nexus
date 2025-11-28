package gg.projecteden.nexus.features.events.waypoints;

import gg.projecteden.nexus.utils.Distance;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.minecraft.world.waypoints.WaypointTransmitter.Connection;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import java.util.UUID;

@Data
@AllArgsConstructor
class WaypointInstance {
	private UUID uuid;
	private ArmorStand armorStand;
	private Connection connection;
	private IWaypoint waypoint;

	public Player getPlayer() {
		return Bukkit.getPlayer(uuid);
	}

	public boolean isSameWorld() {
		return getWaypoint().getLocation().getWorld().equals(getPlayer().getWorld());
	}

	public boolean isNearby() {
		if (armorStand == null)
			return false;

		if (!isSameWorld())
			return false;

		return Distance.distance(getArmorStand().getLocation(), getPlayer().getLocation()).lte(3);
	}

	public void shutdown() {
		connection.disconnect();
		if (armorStand != null) {
			armorStand.remove();
			Dev.GRIFFIN.send("Shut down waypoint " + getWaypoint());
		} else {
			Dev.GRIFFIN.send("Failed to shut down waypoint " + getWaypoint() + " (no armor stand)");
		}
	}
}
