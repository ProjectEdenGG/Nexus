package me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds;

import eden.utils.TimeUtils.Time;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.events.y2021.bearfair21.BearFair21;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class Rides {
	private static final Map<Ride, Boolean> rideMap = new HashMap<>();

	public Rides() {
		// Disable all rides on startup
		for (Ride ride : Ride.values()) {
			rideMap.put(ride, false);
			PlayerUtils.runCommandAsConsole("rideadm " + ride.getId() + " disable");
		}

		// Dynamic enable task
		Tasks.repeat(0, Time.SECOND.x(2), () -> {
			for (Ride ride : Ride.values()) {
				boolean oldStatus = rideMap.get(ride);
				boolean curStatus = ride.getCurrentStatus();
				if (oldStatus == curStatus) continue;

				rideMap.put(ride, curStatus);
				if (curStatus)
					PlayerUtils.runCommandAsConsole("rideadm " + ride.getId() + " enable");
				else
					PlayerUtils.runCommandAsConsole("rideadm " + ride.getId() + " disable");
			}
		});


	}

	@NoArgsConstructor
	@AllArgsConstructor
	public enum Ride {
		CAROUSEL(loc(52, -29), 20),
		CHAIRSWING(loc(12, -93), 20),
		DROPTOWER(loc(147, -37), 20),
		ENTERPRISE(loc(10, -43), 20),
		FERRISWHEEL(loc(77, 22), 30),
		JETS(loc(77, -146), 20),
		PENDULUM(loc(-4, -113), 20),
		SWINGSHIP(loc(13, -16), 20),
		SWINGTOWER(loc(95, -116), 20),
		TEACUPS(loc(80, -86), 20),
		;

		@Getter
		Location location;
		@Getter
		int radius;

		public String getId() {
			return "bf21_" + name().toLowerCase();
		}

		private static Location loc(int x, int z) {
			return new Location(BearFair21.getWorld(), x, 0, z);
		}

		// Ignores y value
		public boolean isWithinRadius(Player player) {
			Location playerLocation = player.getLocation().clone();
			playerLocation.setY(0);
			return playerLocation.distance(this.location) <= this.radius;
		}

		public List<Player> getPlayersInRadius() {
			return BearFair21.getPlayers().stream().filter(this::isWithinRadius).collect(Collectors.toList());
		}

		public boolean getCurrentStatus() {
			return getPlayersInRadius().size() > 0;
		}
	}
}
