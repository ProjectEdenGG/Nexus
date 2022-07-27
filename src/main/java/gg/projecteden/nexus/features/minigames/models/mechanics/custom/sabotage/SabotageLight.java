package gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage;

import gg.projecteden.nexus.utils.LocationUtils;
import org.bukkit.Location;

public record SabotageLight(Location location, int radius) {
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SabotageLight that = (SabotageLight) o;
		return radius == that.radius &&
			LocationUtils.blockLocationsEqual(location, that.location);
	}
}
