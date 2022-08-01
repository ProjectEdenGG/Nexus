package gg.projecteden.nexus.features.listeners;

import gg.projecteden.nexus.features.afk.AFK;
import gg.projecteden.nexus.utils.ActionBarUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import io.papermc.paper.event.player.PlayerDeepSleepEvent;
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

import java.util.*;

public class Sleep implements Listener {
	private static final String PREFIX = StringUtils.getPrefix("Sleep");
	private static final long SPEED = 150;

	private enum State { AWAKE, SLEEPING, SKIPPING }

	private enum SleepWorlds {
		SURVIVAL(State.AWAKE, "survival", "resource"),
		ONEBLOCK(State.AWAKE, "oneblock"),
		SKYBLOCK(State.AWAKE, "skyblock");

		private State state;
		private final List<String> worldNames;

		SleepWorlds(State state, String... worldNames) {
			this.state = state;
			this.worldNames = Arrays.asList(worldNames);
		}

		public static SleepWorlds getSleepWorldsByWorldName(String worldName) {
			for (SleepWorlds sleepWorlds : SleepWorlds.values()) {
				if (sleepWorlds.getWorldNames().contains(worldName))
					return sleepWorlds;
			}
			return null;
		}

		public void setState(State state) {
			this.state = state;
		}

		public State getState() {
			return state;
		}

		public List<String> getWorldNames() {
			return worldNames;
		}
	}

	static {
		Tasks.repeat(0, 1, () -> {
			for (SleepWorlds sleepWorlds : SleepWorlds.values()) {
				long sleeping = 0;
				long active = 0;
				List<World> worlds = new ArrayList<World>();
				for (String worldName : sleepWorlds.getWorldNames()) {
					World world = Bukkit.getWorld(worldName);
					worlds.add(world);
					sleeping += getSleeping(world).size();
					active += getCanSleep(world).size();
				}
				int needed = (int) Math.ceil(active / 2d);

				if (sleeping >= needed && sleepWorlds.getState() != State.SKIPPING) {
					worlds.forEach(Sleep::skipNight);
				} else if (sleepWorlds.getState() == State.SLEEPING) {
					long finalSleeping = sleeping;
					worlds.forEach(world -> {
						world.getPlayers().forEach(player -> ActionBarUtils.sendActionBar(player,
							"Sleepers needed to skip night: &e" + finalSleeping + "&3/&e" + needed));
					});
				}
			}
		});

	}

	private static boolean canSleep(Player player) {
		return !PlayerUtils.isVanished(player) && !AFK.get(player).isTimeAfk() && player.getGameMode() == GameMode.SURVIVAL;
	}

	private static void skipNight(World world) {
		SleepWorlds sleepWorlds = SleepWorlds.getSleepWorldsByWorldName(world.getName());
		sleepWorlds.setState(State.SKIPPING);

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
			sleepWorlds.setState(State.AWAKE);
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

		SleepWorlds sleepWorlds = SleepWorlds.getSleepWorldsByWorldName(world.getName());
		if (sleepWorlds == null)
			return;

		if (sleepWorlds.getState() != State.SKIPPING)
			sleepWorlds.setState(State.SLEEPING);
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
			SleepWorlds sleepWorlds = SleepWorlds.getSleepWorldsByWorldName(world.getName());
			if (sleepWorlds == null)
				return;

			if (getSleeping(world).size() == 0)
				sleepWorlds.setState(State.AWAKE);
		});
	}

	@EventHandler
	public void onDeepSleep(PlayerDeepSleepEvent event) {
		World world = event.getPlayer().getWorld();
		SleepWorlds sleepWorlds = SleepWorlds.getSleepWorldsByWorldName(world.getName());
		if (sleepWorlds == null)
			return;

		event.setCancelled(true);
	}

	private static List<Player> getCanSleep(World world) {
		return OnlinePlayers.where().world(world).get().stream().filter(Sleep::canSleep).toList();
	}

	private static List<Player> getSleeping(World world) {
		return getCanSleep(world).stream().filter(Player::isSleeping).toList();
	}
}
