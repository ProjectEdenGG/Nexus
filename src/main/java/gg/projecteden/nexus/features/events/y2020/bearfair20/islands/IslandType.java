package gg.projecteden.nexus.features.events.y2020.bearfair20.islands;

import gg.projecteden.nexus.features.events.models.BearFairIsland;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import org.bukkit.Location;

import java.util.Set;

public enum IslandType {
	MAIN(new MainIsland()),
	HALLOWEEN(new HalloweenIsland()),
	MINIGAME_NIGHT(new MinigameNightIsland()),
	SUMMER_DOWN_UNDER(new SummerDownUnderIsland()),
	PUGMAS(new PugmasIsland());

	private final BearFairIsland island;

	IslandType(BearFairIsland island) {
		this.island = island;
	}

	public BearFairIsland get() {
		return island;
	}

	public static BearFairIsland getFromLocation(Location location) {
		WorldGuardUtils WGUtils = new WorldGuardUtils(location);
		Set<String> regions = WGUtils.getRegionNamesAt(location);
		for (IslandType island : values()) {
			if (regions.contains(island.get().getRegion()))
				return island.get();
		}

		return null;
	}
}
