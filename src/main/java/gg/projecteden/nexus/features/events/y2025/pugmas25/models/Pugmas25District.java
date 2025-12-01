package gg.projecteden.nexus.features.events.y2025.pugmas25.models;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.events.y2025.pugmas25.features.Pugmas25Districts;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
public enum Pugmas25District {
	MINES("mines"),
	RIDGE("ridge"),
	WEST_VILLAGE("west"),
	EAST_VILLAGE("east"),
	FARM("farm"),
	PORT("port"),
	TRAIN_STATION("train_station"),
	TRAIN_TRACKS("train_tracks"),
	LAKE("lake"),
	FROZEN_LAKE("frozen_lake"),
	FAIRGROUNDS("fair"),
	HOT_SPRINGS("hot_springs"),
	LUMBERYARD("lumberyard"),
	RIVER("river"),
	CAVES("caves"),
	//
	WILDERNESS(null);

	final String regionId;

	public String getName() {
		return StringUtils.camelCase(this);
	}

	public String getRegionId() {
		return Pugmas25Districts.DISTRICT_REGION_PREFIX + regionId;
	}

	public static @Nullable Pugmas25District of(Location location) {
		if (!location.getWorld().equals(Pugmas25.get().getWorld()))
			return null;

		for (ProtectedRegion region : Pugmas25.get().worldguard().getRegionsAt(location)) {
			for (Pugmas25District district : Pugmas25District.values())
				if (region.getId().equalsIgnoreCase(district.getRegionId()))
					return district;
		}

		return Pugmas25District.WILDERNESS;
	}
}
