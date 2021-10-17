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
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.utils.Utils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
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
		if (RandomUtils.chanceOf(90)) {
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

	public static Location getRandomValidLocation(Location center, int radius, boolean ignoreY, int tries, IMobEvent mobEvent, MobOptions mobOptions) {
		Location location;
		Set<Location> triedList = new HashSet<>();
		for (int i = 0; i < tries; i++) {
			location = getRandomLocation(center, radius, ignoreY);
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

	private static @Nullable Location getRandomLocation(Location center, int radius, boolean ignoreY) {
		AtomicReference<Location> result = new AtomicReference<>(null);
		boolean foundLocation = Utils.attempt(25, () -> {
			double x = RandomUtils.randomInt(center.getBlockX() - radius, center.getBlockX() + radius);
			double y = RandomUtils.randomInt(center.getBlockY() - radius, center.getBlockY() + radius);
			double z = RandomUtils.randomInt(center.getBlockZ() - radius, center.getBlockZ() + radius);

			if (ignoreY) {
				result.set(new Location(center.getWorld(), x, y, z).toCenterLocation());
				Location resultY = result.get().clone();
				resultY.setY(0);

				Location centerXZ = center.clone();
				centerXZ.setY(0);

				return centerXZ.distance(resultY) > MobEvents.preventSpawnRadius;
			}

			result.set(new Location(center.getWorld(), x, y, z).toCenterLocation());

			return center.distance(result.get()) > MobEvents.preventSpawnRadius;
		});

		if (!foundLocation)
			return null;

		if (MobEvents.isDebug()) {
			for (Player player : OnlinePlayers.where().world(center.getWorld()).get()) {
				DotEffect.debug(player, result.get());
			}
		}

		return result.get();
	}

	public static boolean failChance(Difficulty playerDifficulty, int defaultChance, int step) {
		int chance = defaultChance;
		switch (playerDifficulty) {
			case HARD -> chance -= step;
			case EXPERT -> chance -= (step * 2);
		}

		return RandomUtils.chanceOf(chance);
	}


	public static void slimeSize(Slime slime, Difficulty difficulty) {
		if (slime instanceof MagmaCube magmaCube)
			MobEventUtils.magmaCubeSize(magmaCube, difficulty);
		else
			MobEventUtils.greenSlimeSize(slime, difficulty);
	}

	public static void greenSlimeSize(Slime slime, Difficulty difficulty) {
		double minSize = 0;
		double maxSize = 2;

		switch (difficulty) {
			case HARD -> {
				minSize += 1;
				maxSize += 0.5;
			}

			case EXPERT -> {
				minSize += 1.5;
				maxSize += 0.5;
			}
		}

		int size = (int) Math.ceil(Math.pow(2.0, RandomUtils.randomDouble(minSize, maxSize)) - 1);
		slime.setSize(size);
	}

	public static void magmaCubeSize(MagmaCube slime, Difficulty difficulty) {
		int minSize = 0;
		int maxSize = 1;

		switch (difficulty) {
			case HARD -> {
				minSize += 0;
				maxSize += 1;
			}

			case EXPERT -> {
				minSize += 1;
				maxSize += 1;
			}
		}

		int size = (int) Math.ceil(Math.pow(2, RandomUtils.randomInt(minSize, maxSize)) - 1);
		slime.setSize(size);
	}
}
