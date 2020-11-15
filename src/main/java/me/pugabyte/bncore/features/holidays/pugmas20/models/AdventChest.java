package me.pugabyte.bncore.features.holidays.pugmas20.models;

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
		PORT("Port"),
		SQUARE("Square"),
		GARDENS("Gardens"),
		ICE("Ice"),
		UNKNOWN("Unknown");

		String name;
	}
}
