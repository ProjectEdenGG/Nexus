package gg.projecteden.nexus.features.events.y2020.halloween20.models;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public enum Pumpkin {

	ONE(new Location(Bukkit.getWorld("safepvp"), 299.0, 56.0, -1949.0),
			new Location(Bukkit.getWorld("safepvp"), 297.0, 15.0, -1920.0)),
	TWO(new Location(Bukkit.getWorld("safepvp"), 281.0, 58.0, -1937.0),
			new Location(Bukkit.getWorld("safepvp"), 296.0, 15.0, -1921.0)),
	THREE(new Location(Bukkit.getWorld("safepvp"), 354.0, 138.0, -1919.0),
			new Location(Bukkit.getWorld("safepvp"), 297.0, 15.0, -1918.0)),
	FOUR(new Location(Bukkit.getWorld("safepvp"), 335.0, 121.0, -1919.0),
			new Location(Bukkit.getWorld("safepvp"), 296.0, 15.0, -1918.0)),
	FIVE(new Location(Bukkit.getWorld("safepvp"), 350.0, 159.0, -1943.0),
			new Location(Bukkit.getWorld("safepvp"), 296.0, 15.0, -1920.0)),
	SIX(new Location(Bukkit.getWorld("safepvp"), 395.0, 156.0, -1941.0),
			new Location(Bukkit.getWorld("safepvp"), 296.0, 15.0, -1919.0)),
	SEVEN(new Location(Bukkit.getWorld("safepvp"), 298.0, 163.0, -1928.0),
			new Location(Bukkit.getWorld("safepvp"), 297.0, 15.0, -1921.0)),
	EIGHT(new Location(Bukkit.getWorld("safepvp"), 282.0, 203.0, -1906.0),
			new Location(Bukkit.getWorld("safepvp"), 297.0, 15.0, -1919.0));

	@Getter
	Location original;
	@Getter
	Location end;

	Pumpkin(Location original, Location end) {
		this.original = original;
		this.end = end;
	}

	public static Pumpkin getByLocation(Location loc) {
		for (Pumpkin pumpkin : values())
			if (pumpkin.original.equals(loc))
				return pumpkin;
		return null;
	}

}
