package me.pugabyte.nexus.features.events.y2021.bearfair21;

import eden.utils.TimeUtils.Time;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.Archery;
import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.Frogger;
import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.Interactables;
import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.Seeker;
import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.MiniGolf;
import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.reflection.ReflectionGame;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.TimeUtils.Timer;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Fairgrounds {
	public Fairgrounds() {
		new Timer("      Interactables", Interactables::new);
		new Timer("      Minigolf", MiniGolf::new);
		new Timer("      Archery", Archery::new);
		new Timer("      Frogger", Frogger::new);
		new Timer("      Seeker", Seeker::new);
		new Timer("      Reflection", ReflectionGame::new);

		// ridesTask();
	}

	private void ridesTask() {
		// Workaround for https://github.com/TheClowner/ccRides-Support/issues/31
		AtomicBoolean resetRides = new AtomicBoolean(false);
		List<String> rides = Arrays.asList("carousel", "chairswing", "droptower", "enterprise", "ferriswheel", "jets", "pendulum", "swingship", "swingtower", "teacups");

		Nexus.log("Reloading all rides in bearfair...");
		Tasks.repeat(Time.SECOND.x(5), Time.SECOND.x(15), () -> {
			int count = BearFair21.getWGUtils().getPlayersInRegion(BearFair21.getRegion()).size();
			if (count == 0)
				resetRides.set(true);
			else if (resetRides.get()) {
				resetRides.set(false);
				for (String ride : rides)
					PlayerUtils.runCommandAsConsole("rideadm bf21_" + ride + " reload");
			}
		});
	}
}
