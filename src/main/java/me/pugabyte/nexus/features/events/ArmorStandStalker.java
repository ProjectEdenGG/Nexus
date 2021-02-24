package me.pugabyte.nexus.features.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.utils.CitizensUtils;
import me.pugabyte.nexus.utils.EntityUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Time;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ArmorStandStalker {
	public static final List<Stalker> stalkers = new ArrayList<Stalker>() {{
		add(new Stalker(new Location(Bukkit.getWorld("survival"), 15, 15, -8), -30, 30)); // Wakka Crate
	}};

	public ArmorStandStalker() {
		Tasks.repeat(Time.SECOND.x(5), Time.TICK.x(2), () -> {
			for (Stalker stalker : stalkers) {
				Location location = stalker.getLocation();
				ArmorStand armorStand = (ArmorStand) EntityUtils.getNearestEntityType(location, EntityType.ARMOR_STAND, 1.5);
				if (armorStand == null)
					continue;

				Player nearestPlayer = (Player) EntityUtils.getNearestEntityType(location, EntityType.PLAYER, 15);
				if (nearestPlayer == null || CitizensUtils.isNPC(nearestPlayer))
					continue;

				EntityUtils.makeArmorStandLookAtPlayer(
						armorStand, nearestPlayer, stalker.getMinYaw(), stalker.getMaxYaw(), stalker.getMinPitch(), stalker.getMaxPitch()
				);

			}
		});
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	private static class Stalker {
		private Location location;
		private Double minPitch = null;
		private Double maxPitch = null;
		private Double minYaw = null;
		private Double maxYaw = null;

		public Stalker(Location location, Double minPitch, Double maxPitch) {
			this.location = location;
			this.minPitch = minPitch;
			this.maxPitch = maxPitch;
		}

		public Stalker(Location location, int minPitch, int maxPitch) {
			this.location = location;
			this.minPitch = (double) minPitch;
			this.maxPitch = (double) maxPitch;
		}
	}
}
