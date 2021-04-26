package me.pugabyte.nexus.features.events;

import eden.utils.TimeUtils.Time;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.utils.EntityUtils;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ArmorStandStalker {
	public static final List<Stalker> stalkers = new ArrayList<Stalker>() {{
		add(new Stalker(new Location(Bukkit.getWorld("survival"), 15, 15, -8), 10, -30, 30)); // Wakka Crate
	}};

	public ArmorStandStalker() {
		Tasks.repeat(Time.SECOND.x(5), Time.TICK.x(2), () -> {
			for (Stalker stalker : stalkers) {
				Location location = stalker.getLocation();
				if (location == null)
					continue;

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
		private int radius = 5;
		private Double minPitch = null;
		private Double maxPitch = null;
		private Double minYaw = null;
		private Double maxYaw = null;

		public Stalker(Location location, int radius, int minPitch, int maxPitch) {
			this.location = location;
			this.radius = radius;
			this.minPitch = (double) minPitch;
			this.maxPitch = (double) maxPitch;
		}
	}
}
