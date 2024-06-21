package gg.projecteden.nexus.features.events.y2024.pugmas24;

import gg.projecteden.nexus.features.events.y2021.bearfair21.fairgrounds.Rides;
import gg.projecteden.nexus.features.events.y2024.pugmas24.advent.Advent24;
import gg.projecteden.nexus.features.events.y2024.pugmas24.fairgrounds.Fairgrounds;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Timer;
import org.bukkit.Location;

import java.time.LocalDate;

public class Pugmas24 {
	public static final String PREFIX = StringUtils.getPrefix("Pugmas 2024");
	public static final String timerId = "      Events.Pugmas24.";

	public static final LocalDate EPOCH = LocalDate.of(2024, 12, 1);
	public static final LocalDate PUGMAS = LocalDate.of(2024, 12, 25);
	public static final LocalDate END = LocalDate.of(2025, 1, 10);

	public static final String WORLD = "buildadmin"; // TODO: FINAL WORLD
	public static final String REGION = "pugmas24"; // TODO: FINAL REGION NAME

	public static final String LORE = "&ePugmas 2024 Item";
	public static final Location warp = Pugmas24Utils.location(0.5, 52, 0.5, 0, 0);
	public static LocalDate TODAY = LocalDate.now();

	public Pugmas24() {
		startup();
	}

	public static void startup() {
		new Timer(timerId + "AdventPresents", Advent24::new);
		new Timer(timerId + "Fairgrounds", Fairgrounds::new);
		Rides.startup();
	}

	public static void shutdown() {
		Advent24.shutdown();
	}

}
