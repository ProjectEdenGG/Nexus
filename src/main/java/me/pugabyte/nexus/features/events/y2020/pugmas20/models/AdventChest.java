package me.pugabyte.nexus.features.events.y2020.pugmas20.models;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import me.pugabyte.nexus.features.events.y2020.pugmas20.Pugmas20;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.WorldGuardUtils;
import org.bukkit.Location;

@Data
public class AdventChest {
	@NonNull int day;
	@NonNull Location location;
	@NonNull District district;

	public AdventChest(int day, Location location) {
		this.day = day;
		this.location = location;
		this.district = District.of(location);
	}

	public AdventChest(int day, Location location, District district) {
		this.day = day;
		this.location = location;
		this.district = district;
	}

	@AllArgsConstructor
	public enum District {
		GARDENS,
		FROZEN,
		HARBOR,
		PLAZA,
		UNKNOWN;

		@Getter
		private static final String region = Pugmas20.getRegion() + "_district_";

		public String getName() {
			return StringUtils.camelCase(name());
		}

		public static District of(Location location) {
			WorldGuardUtils WGUtils = new WorldGuardUtils(location);
			District district = null;
			for (ProtectedRegion region : WGUtils.getRegionsAt(location))
				if (region.getId().contains(District.region))
					district = District.valueOf(region.getId().replace(District.region, "").toUpperCase());

			if (district == null)
				district = District.UNKNOWN;

			return district;
		}
	}
}
