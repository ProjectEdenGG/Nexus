package me.pugabyte.bncore.features.events.y2020.bearfair20.islands;

import me.pugabyte.bncore.utils.WorldGuardUtils;
import org.bukkit.Location;

import java.util.Set;

public enum IslandType {
	MAIN(new MainIsland()),
	HALLOWEEN(new HalloweenIsland()),
	MINIGAME_NIGHT(new MinigameNightIsland()),
	SUMMER_DOWN_UNDER(new SummerDownUnderIsland()),
	PUGMAS(new PugmasIsland());

	private final Island island;

	IslandType(Island island) {
		this.island = island;
	}

	public Island get() {
		return island;
	}

	public static Island getFromLocation(Location location) {
		WorldGuardUtils WGUtils = new WorldGuardUtils(location);
		Set<String> regions = WGUtils.getRegionNamesAt(location);
		for (IslandType island : values()) {
			if (regions.contains(island.get().getRegion()))
				return island.get();
		}

		return null;
	}
}
