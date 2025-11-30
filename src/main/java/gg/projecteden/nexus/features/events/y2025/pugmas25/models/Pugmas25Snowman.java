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
public enum Pugmas25Snowman {
	PLAYER_CABIN(loc(-707, 118, -3122)),
	WEST_VILLAGE(loc(-719, 118, -3153)),
	BRIDGE(loc(-674, 108, -3156)),
	EAST_VILLAGE_HIGH(loc(-623, 108, -3156)),
	EAST_VILLAGE_LOW(loc(-619, 104, -3116)),
	CABINS(loc(-537, 86, -3090)),
	FOUNTAIN(loc(-690, 79, -2939));

	private final Location frameLoc;

	private static Location loc(int x, int y, int z) {
		return Pugmas25.get().location(x, y, z);
	}

	public static @Nullable Pugmas25Snowman fromFrameLocation(Location location) {
		return Arrays.stream(values())
			.filter(snowman -> LocationUtils.isFuzzyEqual(snowman.getFrameLoc(), location))
			.findFirst()
			.orElse(null);
	}
}
