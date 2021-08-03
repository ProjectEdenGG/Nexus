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
				add(Stalker.builder().uuid("d0734ba2-45e9-4e87-97dc-688e036a20b4").world("server").radius(30).build());
				// Main Front Left
				add(Stalker.builder().uuid("9b6298c9-3513-48fa-bf2d-f591268e70db").world("server").radius(30).build());
				// Main Back Right
				add(Stalker.builder().uuid("5a843299-859d-492a-a61d-d0e9dac61575").world("server").radius(30).build());
				// Main Back Left
				add(Stalker.builder().uuid("1f7cfa85-a51b-46db-8ddd-16be8893ed6f").world("server").radius(30).build());
				// Terrarium
				add(Stalker.builder().uuid("997895f8-de5a-4a55-95b5-647fb9fff288").world("server").radius(15).build());
				// Visuals
				add(Stalker.builder().uuid("24279890-0c9a-4558-ba6c-7987e206773b").world("server").radius(15).build());
				// Misc
				add(Stalker.builder().uuid("1427660f-18a5-4547-86ce-91d70e6e97ce").world("server").radius(15).build());
				// Chat
				add(Stalker.builder().uuid("7db21c72-6264-4b6f-94ef-0368989dc568").world("server").radius(15).build());
				// Inventory
				add(Stalker.builder().uuid("dc665efe-a7be-4486-89fe-ac1db3895247").world("server").radius(15).build());
				// Pets
				add(Stalker.builder().uuid("c3b60f46-ab34-48e8-820f-249e7ee03571").world("server").radius(15).build());
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
