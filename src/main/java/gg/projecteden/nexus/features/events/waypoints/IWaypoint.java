package gg.projecteden.nexus.features.events.waypoints;

import org.bukkit.Color;
import org.bukkit.Location;

public interface IWaypoint {
	WaypointIcon getIcon();
	Color getColor();
	Location getLocation();
	boolean isQuestWaypoint();

	default boolean equals(IWaypoint other) {
		return getLocation().equals(other.getLocation()) &&
			getColor().equals(other.getColor()) &&
			getIcon().equals(other.getIcon());
	}

}
