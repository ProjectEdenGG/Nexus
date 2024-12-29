package gg.projecteden.nexus.features.events.y2021.bearfair21.fairgrounds;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.events.y2021.bearfair21.BearFair21;
import gg.projecteden.nexus.features.vanish.Vanish;
import gg.projecteden.nexus.models.bearfair21.BearFair21Config;
import gg.projecteden.nexus.utils.Distance;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Rides {
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
					if (!BearFair21.getConfig().isEnabled(BearFair21Config.BearFair21ConfigOption.RIDES))
						continue;

					PlayerUtils.runCommandAsConsole("rideadm " + ride.getId() + " enable");
				} else
					PlayerUtils.runCommandAsConsole("rideadm " + ride.getId() + " disable");

				rideMap.put(ride, curStatus);
			}
		});

		dropTowerTask();
	}

	private static void dropTowerTask() {
		WorldGuardUtils worldguard = BearFair21.worldguard();
		String rg = BearFair21.getRegion();

		// Drop Tower
		Map<String, Location> towerLights = new HashMap<>() {{
			put(rg + "_droptower_light_1", new Location(BearFair21.getWorld(), 147, 145, -37));
			put(rg + "_droptower_light_2", new Location(BearFair21.getWorld(), 147, 157, -37));
			put(rg + "_droptower_light_3", new Location(BearFair21.getWorld(), 147, 169, -37));
			put(rg + "_droptower_light_4", new Location(BearFair21.getWorld(), 147, 176, -37));
		}};

		List<Location> locations = new ArrayList<>();
		Tasks.repeat(TickTime.SECOND.x(5), TickTime.TICK.x(2), () -> {
			for (String light_region : towerLights.keySet()) {
				Location location = towerLights.get(light_region);
				if (worldguard.getPlayersInRegion(light_region).size() > 0) {
					locations.add(location);
					location.getBlock().setType(Material.REDSTONE_BLOCK);
				} else if (locations.contains(location)) {
					locations.remove(location);
					location.getBlock().setType(Material.AIR);
				}
			}
		});
		//
	}

	@NoArgsConstructor
	@AllArgsConstructor
	public enum Ride {
		CAROUSEL(loc(52, -29), 50),
		CHAIRSWING(loc(12, -93), 50),
		DROPTOWER(loc(147, -37), 50),
		ENTERPRISE(loc(10, -43), 50),
		FERRISWHEEL(loc(77, 22), 50),
		JETS(loc(77, -146), 50),
		PENDULUM(loc(-4, -113), 50),
		SWINGSHIP(loc(13, -16), 50),
		SWINGTOWER(loc(95, -116), 50),
		TEACUPS(loc(80, -86), 50),
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
			return Distance.distance(playerLocation, this.location).lte(this.radius);
		}

		public List<Player> getPlayersInRadius() {
			return BearFair21.getPlayers().stream()
				.filter(this::isWithinRadius)
				.filter(player -> !Vanish.isVanished(player))
				.filter(player -> !player.getGameMode().equals(GameMode.SPECTATOR))
				.collect(Collectors.toList());
		}

		public boolean getCurrentStatus() {
			return getPlayersInRadius().size() > 0;
		}
	}
}
