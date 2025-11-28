package gg.projecteden.nexus.features.events.y2025.pugmas25.quests;

import gg.projecteden.nexus.features.events.waypoints.IWaypoint;
import gg.projecteden.nexus.features.events.waypoints.WaypointIcon;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.hub.Hub;
import gg.projecteden.nexus.utils.ColorType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.Location;

@Getter
@AllArgsConstructor
public enum Pugmas25QuestWaypoint implements IWaypoint {
	TICKET_MASTER_HUB(WaypointIcon.X, ColorType.LIGHT_RED.getBukkitColor(), new Location(Hub.getWorld(), -8.5, 149, 4.5)),
	TRAIN(WaypointIcon.X, ColorType.LIGHT_RED.getBukkitColor(), new Location(Hub.getWorld(), 0, 151, -66)),
	TICKET_MASTER(WaypointIcon.X, ColorType.LIGHT_RED.getBukkitColor(), loc(-697.5, 82, -2959.5)),
	INN(WaypointIcon.X, ColorType.LIGHT_RED.getBukkitColor(), loc(-745.5, 121, -3147.5)),
	CABIN(WaypointIcon.X, ColorType.LIGHT_RED.getBukkitColor(), loc(-699.5, 119, -3120.5)),
	;

	private final WaypointIcon icon;
	private final Color color;
	private final Location location;

	@Override
	public boolean isQuestWaypoint() {
		return true;
	}

	private static Location loc(double x, double y, double z) {
		return Pugmas25.get().location(x, y, z);
	}
}
