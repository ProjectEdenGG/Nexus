package gg.projecteden.nexus.features.events.y2021.pugmas21;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2021.pugmas21.advent.Advent;
import gg.projecteden.nexus.features.events.y2021.pugmas21.models.CandyCaneCannon;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Timer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.time.LocalDate;

public class Pugmas21 {
	public static final String PREFIX = StringUtils.getPrefix("Pugmas 2021");
	public static final LocalDate EPOCH = LocalDate.of(2021, 12, 1);
	public static final LocalDate PUGMAS = LocalDate.of(2021, 12, 25);
	public static final LocalDate END = LocalDate.of(2022, 1, 9);
	public static final String WORLD = "pugmas21";
	public static final String REGION = "pugmas21";
	public static LocalDate TODAY = LocalDate.now();

	public Pugmas21() {
		new Timer("      Events.Pugmas21.CandyCaneCannon", CandyCaneCannon::new);
		new Timer("      Events.Pugmas21.AdventPresents", Advent::new);

		Nexus.getCron().schedule("0 0 * * *", () -> TODAY = TODAY.plusDays(1));
	}

	public static World getWorld() {
		return Bukkit.getWorld(WORLD);
	}

	public static boolean isActive() {
		return isActive(Pugmas21.TODAY);
	}

	public static boolean isActive(LocalDate date) {
		return !date.isBefore(Pugmas21.EPOCH) && !date.isAfter(Pugmas21.END);
	}

	public static boolean isAdvent(LocalDate date) {
		return !date.isBefore(Pugmas21.EPOCH) && !date.isAfter(Pugmas21.PUGMAS);
	}

	public static boolean isPugmasOrAfter() {
		return isPugmasOrAfter(Pugmas21.TODAY);
	}

	public static boolean isPugmasOrAfter(LocalDate date) {
		return date.isAfter(PUGMAS.plusDays(-1));
	}

	public static boolean isAtPugmas(Player player) {
		return isAtPugmas(player.getLocation());
	}

	public static boolean isAtPugmas(Location location) {
		return location.getWorld().equals(getWorld());
	}

}
