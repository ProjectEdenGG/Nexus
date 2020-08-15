package me.pugabyte.bncore.features.holidays.aeveonproject;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Data;
import lombok.Getter;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.holidays.aeveonproject.effects.Effects;
import me.pugabyte.bncore.features.holidays.aeveonproject.sets.SetType;
import me.pugabyte.bncore.utils.Time.Timer;
import me.pugabyte.bncore.utils.WorldEditUtils;
import me.pugabyte.bncore.utils.WorldGuardUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.Set;
import java.util.stream.Collectors;

/*
	TODO:
		PDA - Iron Pressure Plate, includes current objectives
		Radius based armorstand names for NPCs
			- Armorstands assigned to NPC names that mimic the NPCs nameplate, but disappear clientside
			- https://www.spigotmc.org/threads/spawning-in-a-clientside-nametag-using-armor-stands-protocollib-packets.371934/
			- Only toggle the armorstands name on if the player is nearby + has "met" the NPC
			- armorstand data: gravity=deny, invulnerablie=true, equiptment=locked, size=small, visible=false
 */
@Data
public class AeveonProject implements Listener {
	@Getter
	public static final World WORLD = Bukkit.getWorld("Aeveon_Project");
	@Getter
	public static final WorldGuardUtils WGUtils = new WorldGuardUtils(WORLD);
	public static final WorldEditUtils WEUtils = new WorldEditUtils(WORLD);

	public static String PREFIX = "&8&l[&eAeveonProject&8&l] &3";
	public static String ROOT = "Animations/AeveonProject/";

	public AeveonProject() {
		BNCore.registerListener(this);
		new Timer("    Effects", Effects::new);
		new Timer("    Sets", SetType::values);
	}

	public static boolean isInSpace(Player player) {
		Set<ProtectedRegion> regions = WGUtils.getRegionsAt(player.getLocation());
		Set<ProtectedRegion> spaceRegions = regions.stream().filter(region -> region.getId().contains("space")).collect(Collectors.toSet());
		return spaceRegions.size() > 0;
	}

	public static boolean isInRegion(Player player, String protectedRegion) {
		return isInRegion(player, WGUtils.getProtectedRegion(protectedRegion));
	}

	public static boolean isInRegion(Player player, ProtectedRegion protectedRegion) {
		return isInRegion(player.getLocation(), protectedRegion);
	}

	public static boolean isInRegion(Block block, String protectedRegion) {
		return isInRegion(block, WGUtils.getProtectedRegion(protectedRegion));
	}

	public static boolean isInRegion(Block block, ProtectedRegion protectedRegion) {
		return isInRegion(block.getLocation(), protectedRegion);
	}

	public static boolean isInRegion(Location location, String protectedRegion) {
		return isInRegion(location, WGUtils.getProtectedRegion(protectedRegion));
	}

	public static boolean isInRegion(Location location, ProtectedRegion protectedRegion) {
		Set<ProtectedRegion> regions = WGUtils.getRegionsAt(location);
		for (ProtectedRegion region : regions) {
			if (region.equals(protectedRegion))
				return true;
		}
		return false;
	}

	public static boolean isInWorld(Block block) {
		return isInWorld(block.getLocation());
	}

	public static boolean isInWorld(Player player) {
		return isInWorld(player.getLocation());
	}

	public static boolean isInWorld(Location location) {
		return location.getWorld().equals(AeveonProject.getWORLD());

	}
}
