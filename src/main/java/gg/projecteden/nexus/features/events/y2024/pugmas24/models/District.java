package gg.projecteden.nexus.features.events.y2024.pugmas24.models;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.nexus.features.events.y2024.pugmas24.Pugmas24;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.Getter;
import org.bukkit.Location;

public enum District {
	UNKNOWN,
	WEST,
	EAST,
	LAKESIDE,
	;

	@Getter
	private static final String PREFIX = Pugmas24.get().getRegionName() + "_" + District.class.getSimpleName().toLowerCase();

	public String getName() {
		return StringUtils.camelCase(this);
	}

	public String getFullName() {
		return getName() + " District";
	}

	public static District of(Location location) {
		if (!location.getWorld().equals(Pugmas24.get().getWorld()))
			return null;

		for (ProtectedRegion region : Pugmas24.get().worldguard().getRegionsAt(location))
			for (District district : District.values())
				if (region.getId().matches(PREFIX + "_" + district.name().toLowerCase()))
					return district;

		return District.UNKNOWN;
	}
}
