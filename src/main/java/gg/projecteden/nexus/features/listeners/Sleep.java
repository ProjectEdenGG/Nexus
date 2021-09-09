package gg.projecteden.nexus.features.listeners;

import gg.projecteden.nexus.features.afk.AFK;
import gg.projecteden.nexus.utils.ActionBarUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import org.apache.commons.lang.math.NumberRange;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sleep implements Listener {
	private static final String PREFIX = StringUtils.getPrefix("Sleep");
	private static final long SPEED = 150;

	private enum State { SLEEPING, SKIPPING }
	private static final Map<World, State> sleepingWorlds = new HashMap<>();

	static {
		Tasks.repeat(0, 1, () -> {
			for (World world : sleepingWorlds.keySet()) {
				long sleeping = getSleeping(world).size();
				long active = getCanSleep(world).size();
				int needed = (int) Math.ceil(active / 2d);

				if (sleeping >= needed && sleepingWorlds.get(world) != State.SKIPPING)
					skipNight(world);
				else if (sleepingWorlds.get(world) == State.SLEEPING)
					world.getPlayers().forEach(player -> ActionBarUtils.sendActionBar(player,
						"Sleepers needed to skip night: &e" + sleeping + "&3/&e" + needed));
			}
		});
	}

	private static boolean canSleep(Player player) {
		return !PlayerUtils.isVanished(player) && !AFK.get(player).isTimeAfk() && player.getGameMode() == GameMode.SURVIVAL;
	}

	private static void skipNight(World world) {
		sleepingWorlds.put(world, State.SKIPPING);

		world.setStorm(false);
		world.setThundering(false);

		int taskId = Tasks.repeat(0, 1, () ->
			PlayerUtils.getOnlinePlayers(world).forEach(player ->
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
			sleepingWorlds.remove(world);
		});
	}

	@EventHandler
	public void onBedEnter(PlayerBedEnterEvent event) {
		World world = event.getPlayer().getWorld();

		if (!event.getBedEnterResult().equals(PlayerBedEnterEvent.BedEnterResult.OK))
			return;

		if (isDayTime(world))
			return;
		if (!isDaylightCycleEnabled(world))
			return;

		if (!sleepingWorlds.containsKey(world) && sleepingWorlds.get(world) != State.SKIPPING)
			sleepingWorlds.put(world, State.SLEEPING);
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
			if (sleepingWorlds.containsKey(world) && getSleeping(world).size() == 0)
				sleepingWorlds.remove(world);
		});
	}

	private static List<Player> getCanSleep(World world) {
		return PlayerUtils.getOnlinePlayers(world).stream().filter(Sleep::canSleep).toList();
	}

	private static List<Player> getSleeping(World world) {
		return getCanSleep(world).stream().filter(Player::isSleeping).toList();
	}
}
