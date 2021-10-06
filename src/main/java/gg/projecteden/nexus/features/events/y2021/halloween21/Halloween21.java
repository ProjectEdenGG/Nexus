package gg.projecteden.nexus.features.events.y2021.halloween21;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.mobheads.MobHeads;
import gg.projecteden.nexus.models.halloween21.Halloween21PumpkinHeadConfig;
import gg.projecteden.nexus.models.halloween21.Halloween21PumpkinHeadConfigService;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.PacketUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;

import static gg.projecteden.utils.RandomUtils.chanceOf;

public class Halloween21 implements Listener {

	public Halloween21() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		final LivingEntity victim = event.getEntity();
		final Player killer = event.getEntity().getKiller();

		new Halloween21PumpkinHeadConfigService().edit0(config -> config.unpumpkin(victim));

		if (killer == null)
			return;

		// TODO Remove
		if (!Rank.of(killer).isAdmin())
			return;

		if (MobHeads.shouldIgnore(killer, victim))
			return;

		if (!chanceOf(10))
			return;

		event.getDrops().add(Candy.random().getDisplayItem());
	}

	@Getter
	@AllArgsConstructor
	private enum PumpkinableEntity {
		ZOMBIE(15),
		HUSK(15),
		DROWNED(40),
		SKELETON(15),
		STRAY(15),
		WITHER_SKELETON(50),
		VEX(75),
		;

		private int chance;

		public static PumpkinableEntity of(Entity entity) {
			return valueOf(entity.getType().name());
		}

		public static boolean isPumpkinable(Entity entity) {
			try {
				of(entity);
				return true;
			} catch (IllegalArgumentException ex) {
				return false;
			}
		}
	}

	private static final List<WorldGroup> PUMPKINABLE_WORLD_GROUPS = List.of(WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK, WorldGroup.ONEBLOCK);

	@EventHandler
	public void onEntitySpawn(EntitySpawnEvent event) {
		if (!(event.getEntity() instanceof LivingEntity entity))
			return;

		if (!PUMPKINABLE_WORLD_GROUPS.contains(WorldGroup.of(entity)))
			return;
		if (WorldGroup.of(entity) != WorldGroup.SURVIVAL)
			return;

		if (!PumpkinableEntity.isPumpkinable(entity))
			return;

		final PumpkinableEntity pumpkinable = PumpkinableEntity.of(entity);

		if (!chanceOf(pumpkinable.getChance()))
			return;

		new Halloween21PumpkinHeadConfigService().edit0(config -> config.pumpkin(entity));
	}

	static {
		final Halloween21PumpkinHeadConfigService service = new Halloween21PumpkinHeadConfigService();
		final Halloween21PumpkinHeadConfig config = service.get0();

		Tasks.repeat(0, 1, () -> {
			for (WorldGroup worldGroup : PUMPKINABLE_WORLD_GROUPS) {
				for (World world : worldGroup.getWorlds()) {
					final List<Player> players = OnlinePlayers.where().world(world).rank(Rank::isAdmin).get(); // TODO Remove rank

					for (LivingEntity entity : world.getLivingEntities()) {
						if (!config.isPumpkining(entity))
							continue;

						PacketUtils.sendFakeItem(entity, players, config.getPumpkin(entity), EquipmentSlot.HEAD);
					}
				}
			}
		});
	}

}
