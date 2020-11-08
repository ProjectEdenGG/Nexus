package me.pugabyte.bncore.features.holidays.pugmas20.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Location;

@Data
@AllArgsConstructor
public class AdventChest {
	int day;
	Location location;
	String district;
}
