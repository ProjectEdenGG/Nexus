package gg.projecteden.nexus.features.events.y2024.pugmas24;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.nexus.features.events.y2024.pugmas24.advent.Advent24;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Timer;
import gg.projecteden.nexus.utils.WorldEditUtils;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.time.LocalDate;

public class Pugmas24 {
	public static final String PREFIX = StringUtils.getPrefix("Pugmas 2024");
	public static final String timerId = "      Events.Pugmas24.";

	public static final LocalDate EPOCH = LocalDate.of(2024, 12, 1);
	public static final LocalDate PUGMAS = LocalDate.of(2024, 12, 25);
	public static final LocalDate END = LocalDate.of(2025, 1, 10);

	public static final String WORLD = "buildadmin"; // TODO: FINAL WORLD
	public static final String REGION = "pugmas24ba"; // TODO: FINAL REGION NAME

	public static final String LORE = "&ePugmas 2024 Item";
	public static final Location warp = location(0.5, 52, 0.5, 0, 0);
	public static LocalDate TODAY = LocalDate.now();


	public Pugmas24() {
		new Timer(timerId + "AdventPresents", Advent24::new);
		// TODO: INIT
	}

	public static WorldGuardUtils worldguard() {
		return new WorldGuardUtils(getWorld());
	}

	public static WorldEditUtils worldedit() {
		return new WorldEditUtils(getWorld());
	}

	public static void shutdown() {
		Advent24.shutdown();
	}

	public static World getWorld() {
		return Bukkit.getWorld(WORLD);
	}

	public static ProtectedRegion getRegion() {
		return worldguard().getProtectedRegion(REGION);
	}

	public static boolean isActive() {
		return isActive(TODAY);
	}

	public static boolean isActive(LocalDate date) {
		return !date.isBefore(EPOCH) && !date.isAfter(END);
	}

	public static boolean isAdvent(LocalDate date) {
		return !date.isBefore(EPOCH) && !date.isAfter(PUGMAS);
	}

	public static boolean isPugmasOrAfter() {
		return isPugmasOrAfter(TODAY);
	}

	public static boolean isPugmasOrAfter(LocalDate date) {
		return date.isAfter(PUGMAS.plusDays(-1));
	}

	public static boolean isBeforePugmas() {
		return LocalDate.now().isBefore(EPOCH);
	}

	public static boolean isPastPugmas() {
		return LocalDate.now().isAfter(END);
	}

	public static boolean isAtPugmas(Player player) {
		return isAtPugmas(player.getLocation());
	}

	public static boolean isAtPugmas(Location location) {
		return location.getWorld().equals(getWorld());
	}

	public static Location location(double x, double y, double z) {
		return location(x, y, z, 0, 0);
	}

	public static Location location(double x, double y, double z, float yaw, float pitch) {
		return new Location(getWorld(), x, y, z, yaw, pitch);
	}

	public static String region(String region) {
		return REGION + "_" + region;
	}
}
