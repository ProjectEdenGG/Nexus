package gg.projecteden.nexus.features.events.y2024.pugmas24.models;

import gg.projecteden.nexus.features.events.y2024.pugmas24.Pugmas24;
import lombok.AllArgsConstructor;
import org.bukkit.Location;

@AllArgsConstructor
public enum Pugmas24Waystone {
	HOT_SPRINGS(loc(-474, 127, -3060), loc(-474, 127, -3059, 45).toCenterLocation()),
	TRAIN_STATION(null, null),
	FAIRGROUNDS(null, null),
	TOWN(null, null),
	RIDGE(null, null),
	;

	final Location frameLoc;
	final Location warpLoc;

	private static Location loc(int x, int y, int z) {
		return Pugmas24.get().location(x, y, z);
	}

	private static Location loc(int x, int y, int z, int yaw) {
		return Pugmas24.get().location(x, y, z, yaw, 0);
	}

}
