package gg.projecteden.nexus.features.events.mobevents;

import gg.projecteden.nexus.features.events.mobevents.events.NewDayEvent;
import gg.projecteden.nexus.features.events.mobevents.events.NewNightEvent;
import gg.projecteden.nexus.features.events.mobevents.types.common.IMobEvent;
import gg.projecteden.nexus.features.events.mobevents.types.common.WorldSet;
import gg.projecteden.nexus.features.events.mobevents.types.common.WorldSet.Dimension;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

// TODO:
//	- Make events, and spawned mobs uuid list persistent
//	- per player difficulty

public class MobEvents {
	public static final String METADATA_NO_FALL_DAMAGE = "noFallDamage";
	@Getter
	public static Map<WorldSet, IMobEvent> activeEvents = new HashMap<>();
	public static Set<String> enabledWorlds = Set.of("survival", "world");
	public static final int preventSpawnRadius = 18;
	@Getter
	@Setter
	public static boolean debug = false;

	public MobEvents() {
		timeOfDayTask();
		mobSpawnerTask();
		eventStarterTask();
		new MobEventsListener();
	}

	public static void shutdown() {
		for (WorldSet worldSet : activeEvents.keySet()) {
			IMobEvent mobEvent = activeEvents.get(worldSet);
			if (mobEvent == null || !mobEvent.isActive())
				continue;

			mobEvent.setActive(false);
			for (Dimension dimension : mobEvent.getDimensions()) {
				World world = worldSet.get(dimension);
				MobEventUtils.removeEvent(world);
				mobEvent.removeEntities(world, true);
			}
		}

	}

	private void timeOfDayTask() {
		Tasks.repeat(0, TickTime.TICK, () -> {
			for (String worldName : enabledWorlds) {
				final World world = Bukkit.getWorld(worldName);
				if (world == null || world.getPlayerCount() <= 0)
					continue;

				final long ticks = world.getTime();
				if (ticks == 0)
					new NewDayEvent(world).callEvent();
				else if (ticks == 12000)
					new NewNightEvent(world).callEvent();
			}
		});
	}

	private void eventStarterTask() {
		Tasks.repeat(0, TickTime.TICK, () -> {
			for (WorldSet worldSet : activeEvents.keySet()) {
				IMobEvent mobEvent = activeEvents.get(worldSet);
				if (mobEvent == null || mobEvent.isActive())
					continue;

				World world = worldSet.get(Dimension.OVERWORLD);
				if (mobEvent.getModifier().getStartTimes().contains(world.getTime()))
					continue;

				// TODO: switch to scheduled job
				Tasks.wait(mobEvent.getModifier().getDelayTime(), () -> mobEvent.startEvent(world));
			}
		});

	}

	private void mobSpawnerTask() {
		Tasks.repeat(0, TickTime.TICK.x(5), () -> {
			for (WorldSet worldSet : activeEvents.keySet()) {
				IMobEvent mobEvent = activeEvents.get(worldSet);
				if (mobEvent == null || !mobEvent.isActive())
					continue;

				List<Player> players = mobEvent.getAffectingPlayers();
				List<World> affectedWorlds = players.stream().map(Entity::getWorld).toList();

				for (Dimension dimension : mobEvent.getDimensions()) {
					World world = worldSet.get(dimension);
					if (affectedWorlds.contains(world))
						mobEvent.spawnMob(world, players.stream().filter(player -> player.getWorld().equals(world)).toList());
				}
			}
		});
	}
}
