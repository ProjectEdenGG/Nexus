package me.pugabyte.bncore.utils;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Data;
import lombok.NonNull;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import org.bukkit.World;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class WorldGuardUtils {
	@NonNull
	private World world;

	public RegionManager getManager() {
		return WGBukkit.getRegionManager(world);
	}

	public ProtectedRegion getRegion(String name) {
		ProtectedRegion region = getManager().getRegion(name);
		if (region == null)
			throw new InvalidInputException("Region not found");
		return region;
	}

	public Set<ProtectedRegion> getRegionsLike(String name) {
		Map<String, ProtectedRegion> regions = getManager().getRegions();
		return regions.keySet().stream().filter(id -> id.matches(name.toLowerCase())).map(regions::get).collect(Collectors.toSet());
	}

	public ProtectedRegion getRegionLike(String name) {
		Set<ProtectedRegion> matches = getRegionsLike(name);
		if (matches.size() == 0)
			throw new InvalidInputException("No regions found");
		return matches.iterator().next();
	}


}
