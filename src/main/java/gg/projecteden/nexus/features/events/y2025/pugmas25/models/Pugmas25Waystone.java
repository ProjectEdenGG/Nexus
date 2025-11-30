package gg.projecteden.nexus.features.events.y2025.pugmas25.models;

import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.utils.LocationUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum Pugmas25Waystone {
	HOT_SPRINGS(loc(-474, 126, -3060), loc(-474, 126, -3058, 60).toCenterLocation()),
	TRAIN_STATION(loc(-689, 83, -2961), loc(-689, 82, -2966, 180).toCenterLocation()),
	FAIRGROUNDS(loc(-763, 81, -2897), loc(-762, 81, -2896, -45).toCenterLocation()),
	WEST_VILLAGE(loc(-721, 118, -3161), loc(-719, 118, -3159, -28).toCenterLocation()),
	EAST_VILLAGE(loc(-611, 104, -3107), loc(-610, 104, -3104, 0).toCenterLocation()),
	RIDGE(loc(-673, 157, -3223), loc(-671, 157, -3220, -90)),
	MINES(loc(-741, 105, -3161), loc(-740, 105, -3159, 0).toCenterLocation());

	private final Location frameLoc;
	private final Location warpLoc;

	private static Location loc(int x, int y, int z) {
		return Pugmas25.get().location(x, y, z);
	}

	private static Location loc(int x, int y, int z, int yaw) {
		return Pugmas25.get().location(x, y, z, yaw, 0);
	}

	public static @Nullable Pugmas25Waystone fromFrameLocation(Location location) {
		return Arrays.stream(values())
			.filter(waystone -> LocationUtils.isFuzzyEqual(waystone.getFrameLoc(), location))
			.findFirst()
			.orElse(null);
	}
}
