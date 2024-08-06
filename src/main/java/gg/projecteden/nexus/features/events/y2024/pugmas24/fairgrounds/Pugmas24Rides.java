package gg.projecteden.nexus.features.events.y2024.pugmas24.fairgrounds;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.events.y2024.pugmas24.Pugmas24;
import gg.projecteden.nexus.features.vanish.Vanish;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static gg.projecteden.nexus.utils.Distance.distance;

@NoArgsConstructor
public class Pugmas24Rides {

	private static final Map<Ride, Boolean> rideMap = new HashMap<>();

	public static void startup() {
		// Disable all rides on startup
		rideMap.clear();
		for (Ride ride : Ride.values()) {
			rideMap.put(ride, false);
		}

		// Dynamic enable task
		Tasks.repeat(0, TickTime.SECOND.x(2), () -> {
			for (Ride ride : Ride.values()) {
				boolean oldStatus = rideMap.getOrDefault(ride, false);
				boolean curStatus = ride.getCurrentStatus();
				if (oldStatus == curStatus) continue;

				if (curStatus) {
					if (!Pugmas24.isRidesEnabled())
						continue;

					PlayerUtils.runCommandAsConsole("rideadm " + ride.getId() + " enable");
				} else
					PlayerUtils.runCommandAsConsole("rideadm " + ride.getId() + " disable");

				rideMap.put(ride, curStatus);
			}
		});
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public enum Ride {
		CHAIRSWING(loc(-744, -2867), 100),
		FERRISWHEEL(loc(-817, -2918), 100),
		;

		Location location;

		int radius;

		public String getId() {
			return Pugmas24.get().getRegionName() + "_" + name().toLowerCase();
		}

		private static Location loc(int x, int z) {
			return Pugmas24.get().location(x, 0, z);
		}

		// Ignores y value
		public boolean isWithinRadius(Player player) {
			Location playerLocation = player.getLocation().clone();
			playerLocation.setY(0);
			return distance(playerLocation, this.location).lte(this.radius);
		}

		public List<Player> getPlayersInRadius() {
			return Pugmas24.get().getPlayers().stream()
					.filter(this::isWithinRadius)
					.filter(player -> !Vanish.isVanished(player))
					.filter(player -> !player.getGameMode().equals(GameMode.SPECTATOR))
					.collect(Collectors.toList());
		}

		public boolean getCurrentStatus() {
			return !getPlayersInRadius().isEmpty();
		}
	}
}
