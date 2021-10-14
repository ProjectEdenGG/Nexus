package gg.projecteden.nexus.features.events.mobevents;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.mobevents.types.common.DayPhase;
import gg.projecteden.nexus.features.events.mobevents.types.common.IMobEvent;
import gg.projecteden.nexus.features.events.mobevents.types.common.MobEventType;
import gg.projecteden.nexus.features.events.mobevents.types.common.MobOptions;
import gg.projecteden.nexus.features.events.mobevents.types.common.WorldSet;
import gg.projecteden.nexus.features.particles.effects.DotEffect;
import gg.projecteden.nexus.models.difficulty.DifficultyUser.Difficulty;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.utils.Utils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class MobEventUtils {

	public static void debug(String debug) {
		debug(null, debug);
	}

	public static void debug(Player player, String debug) {
		if (player == null) {
			if (MobEvents.isDebug())
				Nexus.log(StringUtils.stripColor(debug));
		} else {
			PlayerUtils.send(player, debug);
		}
	}

	public static void queueEvent(World world, DayPhase dayPhase) {
		queueEvent(world, dayPhase, null);
	}

	public static void queueEvent(World world, DayPhase dayPhase, Player debugger) {
		debug(debugger, "&3Trying to queue an event in &e" + world.getName());
		if (RandomUtils.chanceOf(80)) {
			debug(debugger, " &crandom chance return");
			return;
		}

		queueEvent(world, MobEventType.random(dayPhase), debugger);
	}

	public static void queueEvent(World world, MobEventType type) {
		queueEvent(world, type, null);
	}

	public static void queueEvent(World world, MobEventType type, Player debugger) {
		IMobEvent mobEvent = type.newInstance();
		if (mobEvent == null) {
			debug(debugger, " &cmobEvent == null");
			return;
		}

		if (mobEvent.getAffectingPlayers().size() <= 0) {
			debug(debugger, " &cno affected players");
			return;
		}

		debug(debugger, " &3Queuing MobEvent &e" + mobEvent.getName() + " &3in &e" + world.getName());
		mobEvent.queue(world);
	}


	public static @Nullable <T extends IMobEvent> T getCurrentEvent(@NotNull World world) {
		return (T) MobEvents.activeEvents.get(new WorldSet(world));
	}

	public static void addEvent(World world, IMobEvent mobEvent) {
		MobEvents.getActiveEvents().put(new WorldSet(world), mobEvent);
	}

	public static void removeEvent(World world) {
		MobEvents.getActiveEvents().remove(new WorldSet(world));
	}

	public static Location getRandomValidLocation(Location center, int radius, int tries, IMobEvent mobEvent, MobOptions mobOptions) {
		Location location;
		Set<Location> triedList = new HashSet<>();
		for (int i = 0; i < tries; i++) {
			location = getRandomLocation(center, radius);
			if (location == null)
				continue;

			if (triedList.contains(location))
				continue;

			triedList.add(location);

			location = mobEvent.handleLocation(location, mobOptions);
			if (location == null)
				continue;

			Block block = location.getBlock();
			Block above = block.getRelative(0, 1, 0);
			Block below = block.getRelative(0, -1, 0);

			if (!MaterialTag.ALL_AIR.isTagged(block))
				continue;

			if (!MaterialTag.ALL_AIR.isTagged(above))
				continue;

			if (!mobEvent.isIgnoreFloor() && !below.isSolid())
				continue;

			if (!mobEvent.isIgnoreLight() && block.getLightLevel() > 7)
				continue;

			return location;
		}

		return null;
	}

	private static @Nullable Location getRandomLocation(Location center, int radius) {
		AtomicReference<Location> result = new AtomicReference<>(null);
		boolean foundLocation = Utils.attempt(25, () -> {
			double x = RandomUtils.randomInt(center.getBlockX() - radius, center.getBlockX() + radius);
			double y = RandomUtils.randomInt(center.getBlockY() - radius, center.getBlockY() + radius);
			double z = RandomUtils.randomInt(center.getBlockZ() - radius, center.getBlockZ() + radius);

			result.set(new Location(center.getWorld(), x, y, z).toCenterLocation());
			return center.distance(result.get()) > MobEvents.preventSpawnRadius;
		});

		if (!foundLocation)
			return null;

		if (MobEvents.isDebug())
			DotEffect.debug(Dev.WAKKA.getPlayer(), result.get());

		return result.get();
	}

	public static boolean failChance(Difficulty playerDifficulty, int defaultChance, int step) {
		int chance = defaultChance;
		switch (playerDifficulty) {
			case HARD:
				chance -= step;
			case EXPERT:
				chance -= step;
		}

		return RandomUtils.chanceOf(chance);
	}
}
