package gg.projecteden.nexus.features.events;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2021.bearfair21.BearFair21;
import gg.projecteden.nexus.utils.EntityUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.Time;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ArmorStandStalker {
	// TODO Database
	public static final List<Stalker> stalkers = new ArrayList<>() {{
		switch (Nexus.getEnv()) {
			case TEST ->
				add(Stalker.builder().uuid("c590d410-2604-48bb-aa3b-dd78419bf672").world("world").radius(25).percentage(.1).build());
			case PROD -> {
				// Spawn - Wakka Crate
				add(Stalker.builder().uuid("ab374814-dbda-4d83-a796-87c8037ee7d2").world("survival").build());
				// BearFair21 - Halloween Island
				add(Stalker.builder().uuid("720fc446-7598-4a99-9493-40bc784667dc").world(BearFair21.getWorld()).build());
				// New Spawn - Owl on Fletcher Building
				add(Stalker.builder().uuid("39fa0c4e-76bf-4773-bd32-c61e1cae3fc3").world("buildadmin").radius(25).build());

				// Store Gallery
				// Main Front Right
				add(Stalker.builder().uuid("7233fac4-fe0e-420f-bd0f-08d98850a12f").world("server").radius(30).build());
				// Main Front Left
				add(Stalker.builder().uuid("f36b3e67-8625-417d-b221-cfb7398fd032").world("server").radius(30).build());
				// Main Back Right
				add(Stalker.builder().uuid("36def925-a5d7-4d95-9fdb-4280a12b88c1").world("server").radius(30).build());
				// Main Back Left
				add(Stalker.builder().uuid("b21f06db-5063-4989-9f5e-f950eacb3616").world("server").radius(30).build());
				// Terrarium
				add(Stalker.builder().uuid("082596b4-e945-4807-8f5c-edbceafa7efd").world("server").radius(15).build());
				// Visuals
				add(Stalker.builder().uuid("b6ed6258-c5df-4b39-9b1a-3157724f9696").world("server").radius(15).build());
				// Misc
				add(Stalker.builder().uuid("a24fcd8f-77c5-4f14-b87a-707a13151b77").world("server").radius(15).build());
				// Chat
				add(Stalker.builder().uuid("e7515522-04b7-4a61-8fc4-53ef5d2ebec7").world("server").radius(15).build());
				// Inventory
				add(Stalker.builder().uuid("ceacf6c5-e422-4da6-a228-527bce1f88d7").world("server").radius(15).build());
				// Pets
				add(Stalker.builder().uuid("1829db93-2ae1-4485-a486-9848e8026262").world("server").radius(15).build());
			}
		}
	}};

	public ArmorStandStalker() {
		Tasks.repeat(Time.SECOND.x(5), Time.TICK.x(2), () -> {
			for (Stalker stalker : stalkers) {
				final Entity entity = stalker.getWorld().getEntity(stalker.getUuid());
				if (entity == null || !entity.isValid())
					continue;

				if (!(entity instanceof ArmorStand armorStand))
					return;

				Location location = entity.getLocation();

				Player nearestPlayer = EntityUtils.getNearestEntityType(location, Player.class, stalker.getRadius());
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
		private UUID uuid;
		private World world;
		@Builder.Default
		private int radius = 10;
		@Builder.Default
		private Double minPitch = -30.0;
		@Builder.Default
		private Double maxPitch = 30.0;
		private Double minYaw = null;
		private Double maxYaw = null;
		private Double percentage = null;

		private static class StalkerBuilder {
			public StalkerBuilder uuid(String uuid) {
				return uuid(UUID.fromString(uuid));
			}

			public StalkerBuilder uuid(UUID uuid) {
				this.uuid = uuid;
				return this;
			}

			public StalkerBuilder world(String world) {
				return world(Bukkit.getWorld(world));
			}

			public StalkerBuilder world(World world) {
				this.world = world;
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
			stalker.getPercentage()
		);
	}

}
