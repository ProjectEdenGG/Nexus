package gg.projecteden.nexus.features.events.y2021.pugmas21.models;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.nexus.features.events.y2021.pugmas21.Pugmas21;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;

@AllArgsConstructor
	public enum District {
		GARDENS,
		FROZEN,
		HARBOR,
		PLAZA,
		UNKNOWN;

		@Getter
		private static final String PREFIX = Pugmas21.REGION + "_" + District.class.getSimpleName().toLowerCase();

		public String getName() {
			return StringUtils.camelCase(this);
		}

		public String getFullName() {
			return getName() + " District";
		}

		public static District of(Location location) {
			if (!location.getWorld().equals(Pugmas21.getWorld()))
				return null;

			for (ProtectedRegion region : new WorldGuardUtils(location).getRegionsAt(location))
				for (District district : District.values())
					if (region.getId().matches(PREFIX + "_" + district.name().toLowerCase()))
						return district;

			return District.UNKNOWN;
		}
	}
