package me.pugabyte.nexus.features.events.y2021.bearfair21;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Getter;
import me.pugabyte.nexus.utils.TimeUtils.Timer;
import me.pugabyte.nexus.utils.WorldEditUtils;
import me.pugabyte.nexus.utils.WorldGuardUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class BearFair21 {
	@Getter
	private static final String region = "bearfair21";
	@Getter
	private static final String PREFIX = "&8&l[&eBearFair&8&l] &3";

	public BearFair21() {
		new Timer("    Fairgrounds", Fairgrounds::new);
	}

	public static World getWorld() {
		return Bukkit.getWorld("bearfair21");
	}

	public static WorldGuardUtils getWGUtils() {
		return new WorldGuardUtils(getWorld());
	}

	public static WorldEditUtils getWEUtils() {
		return new WorldEditUtils(getWorld());
	}

	public static ProtectedRegion getProtectedRegion() {
		return getWGUtils().getProtectedRegion(region);
	}

	public static boolean isAtBearFair(Location location) {
		return location.getWorld().equals(getWorld());
	}
}
