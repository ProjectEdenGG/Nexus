package me.pugabyte.nexus.features.events;

import eden.utils.TimeUtils.Time;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.features.events.y2021.bearfair21.BearFair21;
import me.pugabyte.nexus.utils.EntityUtils;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ArmorStandStalker {
	// @formatter:off
	public static final List<Stalker> stalkers = new ArrayList<>() {{
		add(new Stalker("survival", 15, 15, -8, 10)); 				// Spawn - Wakka Crate
		add(new Stalker(BearFair21.getWorld(), 72, 131, -351, 10)); 		// BearFair21 - Halloween Island
	}};
	// @formatter:on;

	public ArmorStandStalker() {
		Tasks.repeat(Time.SECOND.x(5), Time.TICK.x(2), () -> {
			for (Stalker stalker : stalkers) {
				Location location = stalker.getLocation();

				ArmorStand armorStand;
				try {
					armorStand = (ArmorStand) EntityUtils.getNearestEntityType(location, EntityType.ARMOR_STAND, 1.5);
				} catch (Exception e) {
					continue;
				}
				if (armorStand == null)
					continue;

				Player nearestPlayer = (Player) EntityUtils.getNearestEntityType(location, EntityType.PLAYER, stalker.getRadius());
				if (nearestPlayer != null)
					EntityUtils.makeArmorStandLookAtPlayer(armorStand, nearestPlayer, stalker.getMinYaw(),
						stalker.getMaxYaw(), stalker.getMinPitch(), stalker.getMaxPitch());
			}
		});
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	private static class Stalker {
		@NonNull
		private Location location;
		private int radius = 10;
		private Double minPitch = null;
		private Double maxPitch = null;
		private Double minYaw = null;
		private Double maxYaw = null;

		public Stalker(@NotNull String world, int x, int y, int z, int radius) {
			this.location = new Location(Bukkit.getWorld(world), x, y, z);
			this.radius = radius;
			this.minPitch = -30.0;
			this.maxPitch = 30.0;
		}

		public Stalker(@NotNull World world, int x, int y, int z, int radius) {
			this.location = new Location(world, x, y, z);
			this.radius = radius;
			this.minPitch = -30.0;
			this.maxPitch = 30.0;
		}

		public Stalker(@NotNull Location location, int radius) {
			this.location = location;
			this.radius = radius;
			this.minPitch = -30.0;
			this.maxPitch = 30.0;
		}

		public Stalker(@NotNull Location location, int radius, int minPitch, int maxPitch) {
			this.location = location;
			this.radius = radius;
			this.minPitch = (double) minPitch;
			this.maxPitch = (double) maxPitch;
		}
	}
}
