package gg.projecteden.nexus.features.events.y2021.bearfair21.islands;

import gg.projecteden.nexus.features.events.y2021.bearfair21.BearFair21;
import gg.projecteden.nexus.utils.Timer;
import lombok.Getter;
import org.bukkit.Location;

import java.util.Set;

@Getter
public enum IslandType {
	MAIN(MainIsland.class, BearFair21.locationOf(0, 0, -106)),
	HALLOWEEN(HalloweenIsland.class, BearFair21.locationOf(81, 0, -325)),
	MINIGAME_NIGHT(MinigameNightIsland.class, BearFair21.locationOf(-168, 0, -186)),
	SUMMER_DOWN_UNDER(SummerDownUnderIsland.class, BearFair21.locationOf(165, 0, -185)),
	PUGMAS(PugmasIsland.class, BearFair21.locationOf(-83, 0, -328));

	private BearFair21Island island;
	private Location center;

	IslandType(Class<? extends BearFair21Island> island, Location center) {
		new Timer("        BF21.Islands." + name(), () -> {
			try {
				this.island = island.getConstructor().newInstance();
				this.center = center;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
	}

	public BearFair21Island get() {
		return island;
	}

	public static IslandType of(Location location) {
		Set<String> regions = BearFair21.worldguard().getRegionNamesAt(location);
		for (IslandType island : values())
			if (regions.contains(island.get().getRegion()))
				return island;

		return null;
	}

	public static BearFair21Island get(Location location) {
		final IslandType island = of(location);
		if (island != null)
			return island.get();
		return null;
	}

}
