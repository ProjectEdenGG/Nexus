package gg.projecteden.nexus.features.events.y2024.pugmas24;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.nexus.models.godmode.GodmodeService;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.WorldEditUtils;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static gg.projecteden.nexus.features.commands.staff.WorldGuardEditCommand.canWorldGuardEdit;
import static gg.projecteden.nexus.features.vanish.Vanish.isVanished;

public class Pugmas24Utils {

	public static void send(String message, Player to) {
		PlayerUtils.send(to, message);
	}

	public static String isCheatingMsg(Player player) {
		if (canWorldGuardEdit(player)) return "wgedit";
		if (!player.getGameMode().equals(GameMode.SURVIVAL)) return "creative";
		if (player.isFlying()) return "fly";
		if (isVanished(player)) return "vanish";
		if (new GodmodeService().get(player).isActive()) return "godmode";

		return null;
	}

	public static WorldGuardUtils worldguard() {
		return new WorldGuardUtils(getWorld());
	}

	public static WorldEditUtils worldedit() {
		return new WorldEditUtils(getWorld());
	}

	public static World getWorld() {
		return Bukkit.getWorld(Pugmas24.WORLD);
	}

	public static ProtectedRegion getRegion() {
		return worldguard().getProtectedRegion(Pugmas24.REGION);
	}

	public static boolean isActive() {
		return isActive(Pugmas24.TODAY);
	}

	public static boolean isActive(LocalDate date) {
		return !date.isBefore(Pugmas24.EPOCH) && !date.isAfter(Pugmas24.END);
	}

	public static boolean isAdvent(LocalDate date) {
		return !date.isBefore(Pugmas24.EPOCH) && !date.isAfter(Pugmas24.PUGMAS);
	}

	public static boolean isPugmasOrAfter() {
		return isPugmasOrAfter(Pugmas24.TODAY);
	}

	public static boolean isPugmasOrAfter(LocalDate date) {
		return date.isAfter(Pugmas24.PUGMAS.plusDays(-1));
	}

	public static boolean isBeforePugmas() {
		return LocalDate.now().isBefore(Pugmas24.EPOCH);
	}

	public static boolean isPastPugmas() {
		return LocalDate.now().isAfter(Pugmas24.END);
	}

	public static boolean isNotAtPugmas(Player player) {
		return isNotAtPugmas(player.getLocation());
	}

	public static boolean isNotAtPugmas(PlayerInteractEvent event) {
		return isNotAtPugmas(event.getHand(), event.getPlayer());
	}

	public static boolean isNotAtPugmas(PlayerInteractEntityEvent event) {
		return isNotAtPugmas(event.getHand(), event.getPlayer());
	}

	private static boolean isNotAtPugmas(EquipmentSlot slot, Player player) {
		if (!EquipmentSlot.HAND.equals(slot)) return true;

		return isNotAtPugmas(player);
	}

	public static boolean isNotAtPugmas(Location location) {
		return !location.getWorld().equals(getWorld());
	}

	public static boolean isInRegion(Block block, String region) {
		return isInRegion(block.getLocation(), region);
	}

	public static boolean isInRegion(Player player, String region) {
		return isInRegion(player.getLocation(), region);
	}

	public static boolean isInRegion(Location location, String region) {
		return !isNotAtPugmas(location) && worldguard().isInRegion(location, region);
	}

	public static boolean isInRegionRegex(Location location, String regex) {
		return !isNotAtPugmas(location) && !worldguard().getRegionsLikeAt(regex, location).isEmpty();
	}

	public static Location location(double x, double y, double z) {
		return location(x, y, z, 0, 0);
	}

	public static Location location(double x, double y, double z, float yaw, float pitch) {
		return new Location(getWorld(), x, y, z, yaw, pitch);
	}

	public static String region(String region) {
		return Pugmas24.REGION + "_" + region;
	}

	public static Set<Player> getPlayers() {
		return new HashSet<>(OnlinePlayers.where().world(getWorld()).get());
	}

	public static Set<Player> getPlayersIn(ProtectedRegion region) {
		return getPlayersIn(region.getId());
	}

	public static Set<Player> getPlayersIn(String region) {
		return new HashSet<>(OnlinePlayers.where().world(getWorld()).region(region).get());
	}
}
