package me.pugabyte.bncore.features.events.y2020.pugmas20.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Location;

@Data
@AllArgsConstructor
public class AdventChest {
	@NonNull int day;
	@NonNull Location location;
	@NonNull District district;

	@Getter
	@AllArgsConstructor
	public enum District {
		HARBOR("Harbor"),
		PLAZA("Plaza"),
		GARDENS("Gardens"),
		FROZEN("Frozen"),
		UNKNOWN("Unknown");

		String name;
	}
}
