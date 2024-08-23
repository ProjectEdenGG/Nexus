package gg.projecteden.nexus.features.events.y2024.pugmas24.fairgrounds;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.events.y2024.pugmas24.Pugmas24;
import gg.projecteden.nexus.features.events.y2024.pugmas24.fairgrounds.frogger.Pugmas24Frogger;
import gg.projecteden.nexus.features.events.y2024.pugmas24.fairgrounds.reflection.Pugmas24Reflection;
import gg.projecteden.nexus.features.events.y2024.pugmas24.fairgrounds.slotmachine.Pugmas24SlotMachine;
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

public class Pugmas24Fairgrounds {
	public static final Location minigolfAnimationLoc = Pugmas24.get().location(-712, 67, -2883);

	public Pugmas24Fairgrounds() {
		rides();

		new Pugmas24WhacAMole();
		new Pugmas24SlotMachine();

		new Pugmas24Frogger();
		new Pugmas24Reflection();
		new Pugmas24Minigolf();
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
		FERRISWHEEL(loc(-788, -2900), 100),
		SWINGTOWER(loc(-744, -2867), 100),
		PENDULUM(loc(-747, -2834), 50),
		TEACUPS(loc(-729, -2919), 50),
		CAROUSEL(loc(-729, -2885), 50),
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
