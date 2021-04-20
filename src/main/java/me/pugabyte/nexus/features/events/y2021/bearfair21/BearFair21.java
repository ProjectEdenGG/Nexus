package me.pugabyte.nexus.features.events.y2021.bearfair21;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Getter;
import me.pugabyte.nexus.models.godmode.Godmode;
import me.pugabyte.nexus.models.godmode.GodmodeService;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.TimeUtils.Timer;
import me.pugabyte.nexus.utils.WorldEditUtils;
import me.pugabyte.nexus.utils.WorldGuardUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import static me.pugabyte.nexus.utils.PlayerUtils.isVanished;


public class BearFair21 {
	@Getter
	private static final boolean allowWarp = false;
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

	public static boolean isAtBearFair(Entity entity) {
		return isAtBearFair(entity.getLocation());
	}

	public static boolean isAtBearFair(Player player) {
		return isAtBearFair(player.getLocation());
	}

	public static boolean isAtBearFair(Location location) {
		return location.getWorld().equals(getWorld());
	}

	public static boolean isInRegion(Block block, String region) {
		return isInRegion(block.getLocation(), region);
	}

	public static boolean isInRegion(Player player, String region) {
		return isInRegion(player.getLocation(), region);
	}

	public static boolean isInRegion(Location location, String region) {
		return location.getWorld().equals(getWorld()) && getWGUtils().isInRegion(location, region);
	}

	public static void send(String message, Player to) {
		PlayerUtils.send(to, message);
	}

	public static String isCheatingMsg(Player player) {
		if (player.hasPermission("worldguard.region.bypass.*")) return "wgedit";
		if (!player.getGameMode().equals(GameMode.SURVIVAL)) return "creative";
		if (player.isFlying()) return "fly";
		if (isVanished(player)) return "vanish";
		if (((Godmode) new GodmodeService().get(player)).isEnabled()) return "godemode";

		return null;
	}
}
