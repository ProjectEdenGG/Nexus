package me.pugabyte.nexus.features.events;

import eden.utils.Env;
import eden.utils.TimeUtils.Time;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.events.y2021.bearfair21.BearFair21;
import me.pugabyte.nexus.utils.EntityUtils;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ArmorStandStalker {
	// @formatter:off
	public static final List<Stalker> stalkers = new ArrayList<>() {{
		if(Nexus.getEnv() == Env.TEST) {
			add(Stalker.builder().location("world", -209, 76, 293).radius(25).interval(1.5).build());			// test server
		} else if(Nexus.getEnv() == Env.PROD) {
			add(Stalker.builder().location("survival", 15, 15, -8).build()); 						// Spawn - Wakka Crate
			add(Stalker.builder().location(BearFair21.getWorld(), 72, 131, -351).build()); 				// BearFair21 - Halloween Island
			add(Stalker.builder().location("buildadmin", -2205, 69, -223).radius(25).build());		// New Spawn - Owl on Fletcher Building
		}
	}};
	// @formatter:on;

	public ArmorStandStalker() {
		Tasks.repeat(Time.SECOND.x(5), Time.TICK.x(20), () -> {
			for (Stalker stalker : stalkers) {
				Location location = stalker.getLocation();

				ArmorStand armorStand;
				try {
					armorStand = (ArmorStand) EntityUtils.getNearestEntityType(location, EntityType.ARMOR_STAND, 2);
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}

				if (armorStand == null)
					continue;

				Player nearestPlayer = (Player) EntityUtils.getNearestEntityType(location, EntityType.PLAYER, stalker.getRadius());
				if (nearestPlayer != null)
					lookAt(armorStand, stalker, nearestPlayer);
			}
		});
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	private static class Stalker {
		@NonNull
		private Location location;
		private int radius = 10;
		private Double minPitch = -30.0;
		private Double maxPitch = 30.0;
		private Double minYaw = null;
		private Double maxYaw = null;
		private Double interval = null;

		private static class StalkerBuilder {
			public StalkerBuilder location(String world, int x, int y, int z) {
				return location(Bukkit.getWorld(world), x, y, z);
			}

			public StalkerBuilder location(World world, int x, int y, int z) {
				return location(new Location(world, x, y, z));
			}

			public StalkerBuilder location(Location location) {
				this.location = location;
				return this;
			}
		}
	}

	private void lookAt(ArmorStand armorStand, Stalker stalker, Player player) {
		EntityUtils.makeArmorStandLookAtPlayer(
			armorStand,
			player,
			stalker.getMinYaw(),
			stalker.getMaxYaw(),
			stalker.getMinPitch(),
			stalker.getMaxPitch(),
			stalker.getInterval()
		);
	}
}
