package gg.projecteden.nexus.features.survival;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.afk.AFK;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.utils.ActionBarUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.StringUtils;
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
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Sleep extends Feature implements Listener {
	private static final String PREFIX = StringUtils.getPrefix("Sleep");
	private static final long SPEED = 150;

	private enum State { AWAKE, SLEEPING, SKIPPING }

	@Getter
	public enum TimeSyncedWorldGroup implements IWorldGroup {
		SURVIVAL(State.AWAKE, "survival", "resource"),
		ONEBLOCK(State.AWAKE, "oneblock_world"),
		SKYBLOCK(State.AWAKE, "bskyblock_world");

		@Setter
		private State state;
		private final List<String> worldNames;

		TimeSyncedWorldGroup(State state, String... worldNames) {
			this.state = state;
			this.worldNames = Arrays.asList(worldNames);
		}

		public static TimeSyncedWorldGroup of(String worldName) {
			for (TimeSyncedWorldGroup worldGroup : TimeSyncedWorldGroup.values()) {
				if (worldGroup.getWorldNames().contains(worldName))
					return worldGroup;
			}
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
				List<World> worlds = new ArrayList<>();
				for (World world : worldGroup.getWorlds()) {
					worlds.add(world);
					sleeping += getSleeping(world).size();
					active += getCanSleep(world).size();
				}
				int needed = (int) Math.ceil(active / 2d);

				if (worldGroup.getState() == State.SLEEPING) {
					if (sleeping >= needed) {
						worlds.forEach(Sleep::skipNight);
					} else {
						long finalSleeping = sleeping;
						worlds.forEach(world -> {
							world.getPlayers().forEach(player -> ActionBarUtils.sendActionBar(player,
								"Sleepers needed to skip night: &e" + finalSleeping + "&3/&e" + needed));
						});
					}
				}
			}
		});

	}

	private static boolean canSleep(Player player) {
		return !PlayerUtils.isVanished(player) && !AFK.get(player).isTimeAfk() && player.getGameMode() == GameMode.SURVIVAL;
	}

	private static void skipNight(World world) {
		TimeSyncedWorldGroup worldGroup = TimeSyncedWorldGroup.of(world.getName());
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

		if (!isValidWorld(world))
			return;
		if (!event.getBedEnterResult().equals(PlayerBedEnterEvent.BedEnterResult.OK))
			return;
		if (isDayTime(world))
			return;
		if (!isDaylightCycleEnabled(world))
			return;
		if (!canSleep(event.getPlayer()))
			return;

		TimeSyncedWorldGroup worldGroup = TimeSyncedWorldGroup.of(world.getName());
		if (worldGroup == null)
			return;

		if (worldGroup.getState() != State.SKIPPING)
			worldGroup.setState(State.SLEEPING);
	}

	private boolean isValidWorld(World world) {
		return world.getName().contains("survival") || world.getName().contains("resource");
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
			TimeSyncedWorldGroup worldGroup = TimeSyncedWorldGroup.of(world.getName());
			if (worldGroup == null)
				return;

			if (getSleeping(world).size() == 0)
				worldGroup.setState(State.AWAKE);
		});
	}

	@EventHandler
	public void onDeepSleep(PlayerDeepSleepEvent event) {
		World world = event.getPlayer().getWorld();
		TimeSyncedWorldGroup worldGroup = TimeSyncedWorldGroup.of(world.getName());
		if (worldGroup == null)
			return;

		event.setCancelled(true);
	}

	private static List<Player> getCanSleep(World world) {
		return OnlinePlayers.where().world(world).get().stream().filter(Sleep::canSleep).toList();
	}

	private static List<Player> getSleeping(World world) {
		return getCanSleep(world).stream().filter(Player::isSleeping).toList();
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
			TimeSyncedWorldGroup worldGroup = TimeSyncedWorldGroup.of(world.getName());
			if (worldGroup == null)
				return;

			syncWorlds(worldGroup);
		}
	}
}
