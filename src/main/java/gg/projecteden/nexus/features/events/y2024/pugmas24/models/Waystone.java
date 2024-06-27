package gg.projecteden.nexus.features.events.y2024.pugmas24.models;

import gg.projecteden.nexus.features.events.y2024.pugmas24.Pugmas24;
import lombok.AllArgsConstructor;
import org.bukkit.Location;

@AllArgsConstructor
public enum Waystone {
	HOT_SPRINGS(
			Pugmas24.get().location(-474, 127, -3060),
			Pugmas24.get().location(-474, 127, -3059, 45, 0).toCenterLocation()),

	TRAIN_STATION(null, null),

	FAIRGROUNDS(null, null),

	TOWN(null, null),

	RIDGE(null, null);

	final Location frameLoc;
	final Location warpLoc;

}
