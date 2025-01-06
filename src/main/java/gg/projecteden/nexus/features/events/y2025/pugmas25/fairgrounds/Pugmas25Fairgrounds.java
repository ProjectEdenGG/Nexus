package gg.projecteden.nexus.features.events.y2025.pugmas25.fairgrounds;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.events.y2025.pugmas25.fairgrounds.frogger.Pugmas25Frogger;
import gg.projecteden.nexus.features.events.y2025.pugmas25.fairgrounds.reflection.Pugmas25Reflection;
import gg.projecteden.nexus.features.events.y2025.pugmas25.fairgrounds.slotmachine.Pugmas25SlotMachine;
import gg.projecteden.nexus.features.vanish.Vanish;
import gg.projecteden.nexus.utils.Distance;
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

public class Pugmas25Fairgrounds {

	public Pugmas25Fairgrounds() {
		rides();

		new Pugmas25WhacAMole();
		new Pugmas25SlotMachine();

		new Pugmas25Frogger();
		new Pugmas25Reflection();
		new Pugmas25Minigolf();
	}

	private static final Map<Ride, Boolean> rideMap = new HashMap<>();

	private static void rides() {
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
					if (!Pugmas25.isRidesEnabled())
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
		FERRISWHEEL(loc(-788, -2900), 100),
		SWINGTOWER(loc(-744, -2867), 100),
		PENDULUM(loc(-747, -2834), 50),
		TEACUPS(loc(-729, -2919), 50),
		CAROUSEL(loc(-729, -2885), 50),
		;

		Location location;

		int radius;

		public String getId() {
			return Pugmas25.get().getRegionName() + "_" + name().toLowerCase();
		}

		private static Location loc(int x, int z) {
			return Pugmas25.get().location(x, 0, z);
		}

		// Ignores y value
		public boolean isWithinRadius(Player player) {
			Location playerLocation = player.getLocation().clone();
			playerLocation.setY(0);
			return Distance.distance(playerLocation, this.location).lte(this.radius);
		}

		public List<Player> getPlayersInRadius() {
			return Pugmas25.get().getPlayers().stream()
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
