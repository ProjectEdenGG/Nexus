package gg.projecteden.nexus.features.survival;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.afk.AFK;
import gg.projecteden.nexus.features.vanish.Vanish;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.utils.ActionBarUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.worldgroup.IWorldGroup;
import io.papermc.paper.event.player.PlayerDeepSleepEvent;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.math.NumberRange;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

public class Sleep extends Feature implements Listener {
	public static final Map<World, Map<UUID, LocalDateTime>> recentQuits = new HashMap<>();
	private static final long SPEED = 150;

	private enum State { AWAKE, SLEEPING, SKIPPING }

	@Getter
	public enum TimeSyncedWorldGroup implements IWorldGroup {
		SURVIVAL(State.AWAKE, "survival", "resource"),
		ONEBLOCK(State.AWAKE, "oneblock_world"),
		SKYBLOCK(State.AWAKE, "bskyblock_world");

		@Setter
		@SuppressWarnings("NonFinalFieldInEnum")
		private State state;
		private final List<String> worldNames;

		TimeSyncedWorldGroup(State state, String... worldNames) {
			this.state = state;
			this.worldNames = Arrays.asList(worldNames);
		}

		public static TimeSyncedWorldGroup of(World world) {
			for (TimeSyncedWorldGroup worldGroup : TimeSyncedWorldGroup.values())
				if (worldGroup.contains(world))
					return worldGroup;

			return null;
		}
	}

	@Override
	public void onStart() {
		for (TimeSyncedWorldGroup worldGroup : TimeSyncedWorldGroup.values())
			WorldTimeSync.syncWorlds(worldGroup);
	}

	static {
		Tasks.repeat(0, 1, () -> {
			for (TimeSyncedWorldGroup worldGroup : TimeSyncedWorldGroup.values()) {
				long sleeping = 0;
				long active = 0;

				for (World world : worldGroup.getWorlds()) {
					sleeping += getSleepingCount(world);
					active += getCanSleepCount(world);
				}

				int needed = (int) Math.ceil(active / 2d);

				if (worldGroup.getState() == State.SLEEPING)
					if (sleeping >= needed)
						worldGroup.getWorlds().forEach(Sleep::skipNight);
					else
						for (Player player : worldGroup.getPlayers())
							ActionBarUtils.sendActionBar(player, "Sleepers needed to skip night: &e" + sleeping + "&3/&e" + needed);
			}
		});
	}

	private static boolean canSleep(Player player) {
		return !Vanish.isVanished(player) && !AFK.get(player).isTimeAfk() && player.getGameMode() == GameMode.SURVIVAL;
	}

	private static void skipNight(World world) {
		var worldGroup = TimeSyncedWorldGroup.of(world);
		if (worldGroup == null)
			return;

		worldGroup.setState(State.SKIPPING);

		world.setStorm(false);
		world.setThundering(false);

		int taskId = Tasks.repeat(0, 1, () -> OnlinePlayers.where().world(world).get().forEach(player ->
			ActionBarUtils.sendActionBar(player, "The night was skipped because 50% of players slept")));
		
		int wait = 0;
		while (true) {
			long newTime = world.getTime() + (++wait * SPEED);
			if (!new NumberRange(12541L, (24000L - SPEED)).containsNumber(newTime))
				break;

			Tasks.wait(wait, () -> world.setTime(newTime));
		}

		Tasks.wait(wait, () -> {
			world.setTime(0);
			if (world.hasStorm())
				world.setStorm(false);
			if (world.isThundering())
				world.setThundering(false);
			Tasks.cancel(taskId);
			WorldTimeSync.syncWorlds(worldGroup);
			worldGroup.setState(State.AWAKE);
		});
	}

	@EventHandler
	public void onBedEnter(PlayerBedEnterEvent event) {
		World world = event.getPlayer().getWorld();

		var worldGroup = TimeSyncedWorldGroup.of(world);
		if (worldGroup == null)
			return;

		if (!event.getBedEnterResult().equals(PlayerBedEnterEvent.BedEnterResult.OK))
			return;
		if (isDayTime(world))
			return;
		if (!isDaylightCycleEnabled(world))
			return;
		if (!canSleep(event.getPlayer()))
			return;

		if (worldGroup.getState() != State.SKIPPING)
			worldGroup.setState(State.SLEEPING);
	}

	private boolean isDayTime(World world) {
		return !(world.getTime() >= 12541 && world.getTime() <= 23458);
	}

	private boolean isDaylightCycleEnabled(World world) {
		Boolean gameRuleValue = world.getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE);
		return gameRuleValue != null && gameRuleValue;
	}

	@EventHandler
	public void onBedLeave(PlayerBedLeaveEvent event) {
		Tasks.wait(1, () -> {
			World world = event.getPlayer().getWorld();
			var worldGroup = TimeSyncedWorldGroup.of(world);
			if (worldGroup == null)
				return;

			if (getSleepingCount(world) == 0)
				worldGroup.setState(State.AWAKE);
		});
	}

	@EventHandler
	public void onDeepSleep(PlayerDeepSleepEvent event) {
		World world = event.getPlayer().getWorld();
		var worldGroup = TimeSyncedWorldGroup.of(world);
		if (worldGroup == null)
			return;

		event.setCancelled(true);
	}

	@NotNull
	private static Stream<Player> getCanSleep(World world) {
		return OnlinePlayers.where().world(world).get().stream().filter(Sleep::canSleep);
	}

	private static int getCanSleepCount(World world) {
		final long online = getCanSleep(world).count();
		final long offline = janitorOffline(world).size();

		return (int) (online + offline);
	}

	private static int getSleepingCount(World world) {
		return (int) getCanSleep(world).filter(Player::isSleeping).count();
	}

	private static Map<UUID, LocalDateTime> janitorOffline(World world) {
		final var twoMinutesAgo = LocalDateTime.now().minusMinutes(2);

		final var map = recentQuits.getOrDefault(world, new HashMap<>());
		new HashMap<>(map).forEach((uuid, timestamp) -> {
			if (timestamp.isBefore(twoMinutesAgo))
				map.remove(uuid);
		});

		return map;
	}

	@EventHandler
	public void on(PlayerQuitEvent event) {
		final var player = event.getPlayer();
		if (canSleep(player))
			recentQuits.computeIfAbsent(player.getWorld(), $ -> new HashMap<>()).put(player.getUniqueId(), LocalDateTime.now());
	}

	@EventHandler
	public void on(PlayerJoinEvent event) {
		for (var entry : recentQuits.entrySet())
			entry.getValue().remove(event.getPlayer().getUniqueId());
	}

	public static class WorldTimeSync implements Listener {
		public static void syncWorlds(@Nullable TimeSyncedWorldGroup worldGroup, World baseWorld) {
			if (worldGroup == null)
				return;
			if (baseWorld == null)
				return;

			for (World currentWorld : worldGroup.getWorlds()) {
				if (currentWorld.equals(baseWorld))
					continue;

				Nexus.log("[Sleep] Syncing " + currentWorld.getName() + " with " + baseWorld.getName() + " at time " + currentWorld.getTime());
				currentWorld.setTime(baseWorld.getTime());
			}
		}

		public static void syncWorlds(@Nullable TimeSyncedWorldGroup worldGroup) {
			if (worldGroup == null)
				return;

			syncWorlds(worldGroup, Bukkit.getWorld(worldGroup.getWorldNames().get(0)));
		}

		@EventHandler
		public void onWorldChange(PlayerChangedWorldEvent event) {
			World world = event.getPlayer().getWorld();
			var worldGroup = TimeSyncedWorldGroup.of(world);
			if (worldGroup == null)
				return;

			syncWorlds(worldGroup);
		}
	}

}
