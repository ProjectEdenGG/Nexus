package gg.projecteden.nexus.features.events.waypoints;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Color;
import org.bukkit.Location;

@Data
@AllArgsConstructor
public class CustomWaypoint implements IWaypoint {
	private WaypointIcon icon;
	private Color color;
	private Location location;
	private final boolean isQuestWaypoint = false;

}
