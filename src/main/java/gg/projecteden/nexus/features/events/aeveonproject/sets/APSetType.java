package gg.projecteden.nexus.features.events.aeveonproject.sets;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.nexus.features.events.aeveonproject.sets.lobby.Lobby;
import gg.projecteden.nexus.features.events.aeveonproject.sets.sialia.Sialia;
import gg.projecteden.nexus.features.events.aeveonproject.sets.sialiaCrashing.SialiaCrashing;
import gg.projecteden.nexus.features.events.aeveonproject.sets.sialiaWreckage.SialiaWreckage;
import gg.projecteden.nexus.features.events.aeveonproject.sets.vespyr.Vespyr;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import org.bukkit.Location;

public enum APSetType {
	LOBBY(new Lobby()),
	SIALIA(new Sialia()),
	SIALIA_CRASHING(new SialiaCrashing()),
	SIALIA_WRECKAGE(new SialiaWreckage()),
	VESPYR(new Vespyr());

	private final APSet APSet;

	APSetType(APSet APSet) {
		this.APSet = APSet;
	}

	public APSet get() {
		return APSet;
	}

	public static APSet getFromLocation(Location location) {
		WorldGuardUtils WGUtils = new WorldGuardUtils(location);
		java.util.Set<String> regions = WGUtils.getRegionNamesAt(location);
		for (APSetType set : values()) {
			if (regions.contains(set.get().getRegion()))
				return set.get();
		}

		return null;
	}

	public static APSet getFromRegion(ProtectedRegion region) {
		return getFromRegion(region.getId());
	}

	public static APSet getFromRegion(String id) {
		for (APSetType setType : values()) {
			String region = setType.get().getRegion();
			if (region != null && region.equalsIgnoreCase(id)) {
				return setType.get();
			}
		}

		return null;
	}
}
