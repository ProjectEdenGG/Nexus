package me.pugabyte.bncore.features.holidays.aeveonproject.sets;

import me.pugabyte.bncore.features.holidays.aeveonproject.sets.sialia.Sialia;
import me.pugabyte.bncore.features.holidays.aeveonproject.sets.sialiaCrashing.SialiaCrashing;
import me.pugabyte.bncore.features.holidays.aeveonproject.sets.sialiaWreckage.SialiaWreckage;
import me.pugabyte.bncore.utils.WorldGuardUtils;
import org.bukkit.Location;

public enum SetType {
	SIALIA(new Sialia()),
	SIALIA_CRASHING(new SialiaCrashing()),
	SIALIA_WRECKAGE(new SialiaWreckage());

	private final Set set;

	SetType(Set set) {
		this.set = set;
	}

	public Set get() {
		return set;
	}

	public static Set getFromLocation(Location location) {
		WorldGuardUtils WGUtils = new WorldGuardUtils(location);
		java.util.Set<String> regions = WGUtils.getRegionNamesAt(location);
		for (SetType set : values()) {
			if (regions.contains(set.get().getRegion()))
				return set.get();
		}

		return null;
	}
}
