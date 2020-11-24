package me.pugabyte.nexus.features.events.y2020.pugmas20.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import me.pugabyte.nexus.features.events.y2020.pugmas20.AdventChests;
import me.pugabyte.nexus.utils.StringUtils;
import org.bukkit.Location;

@Data
public class AdventChest {
	@NonNull int day;
	@NonNull Location location;
	@NonNull District district;

	public AdventChest(int day, Location location) {
		this.day = day;
		this.location = location;
		this.district = AdventChests.getDistrict(location);
	}

	public AdventChest(int day, Location location, District district) {
		this.day = day;
		this.location = location;
		this.district = district;
	}

	@AllArgsConstructor
	public enum District {
		HARBOR,
		PLAZA,
		GARDENS,
		FROZEN,
		UNKNOWN;

		public String getName() {
			return StringUtils.camelCase(name());
		}
	}
}
