package me.pugabyte.nexus.features.listeners;

import me.lexikiq.HasPlayer;
import me.pugabyte.nexus.features.afk.AFK;
import me.pugabyte.nexus.features.store.perks.joinquit.VanishEvent;
import me.pugabyte.nexus.utils.ActionBarUtils;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Tasks;
import org.apache.commons.lang.math.NumberRange;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

public class Sleep implements Listener {
	private static final String PREFIX = StringUtils.getPrefix("Sleep");
	public boolean handling = false;
	public long lastCalculatedSleeping = 0;
	public long lastCalculatedNeeded = 0;
	public final long speed = 150;

	public void calculate(World world) {
		if (!(world.getTime() >= 12541 && world.getTime() <= 23458))
			return;

		Boolean gameRuleValue = world.getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE);
		if (gameRuleValue != null && !gameRuleValue)
			return;

		List<Player> players = world.getPlayers();
		long sleeping = players.stream().filter(player -> player.isSleeping() && this.canSleep(player)).count();
		long active = players.stream().filter(this::canSleep).count();

		if (sleeping == 0) {
			lastCalculatedSleeping = sleeping;
			return;
		}

		int needed = (int) Math.ceil(active / 2d);

		if (sleeping != lastCalculatedSleeping || needed != lastCalculatedNeeded)
			for (Player player : players)
				Tasks.wait(2, () -> ActionBarUtils.sendActionBar(player, "Sleepers needed to skip night: &e" + sleeping + "&3/&e" + needed));

		lastCalculatedSleeping = sleeping;
		lastCalculatedNeeded = needed;

		if (sleeping >= needed) {
			handling = true;
			world.setStorm(false);
			world.setThundering(false);

			players.forEach(player -> PlayerUtils.send(player, PREFIX + "The night was skipped because 50% of players slept!"));

			int wait = 0;
			while (true) {
				long newTime = world.getTime() + (++wait * speed);
				if (!new NumberRange(12541L, (24000L - speed)).containsNumber(newTime))
					break;

				Tasks.wait(wait, () -> world.setTime(newTime));
			}

			Tasks.wait(wait, () -> {
				world.setTime(0);
				if (world.hasStorm())
					world.setStorm(false);
				if (world.isThundering())
					world.setThundering(false);

				handling = false;
				lastCalculatedSleeping = 0;
				lastCalculatedNeeded = 0;
			});
		}
	}

	protected boolean canSleep(Player player) {
		return !PlayerUtils.isVanished(player) && !AFK.get(player).isTimeAfk() && player.getGameMode() == GameMode.SURVIVAL;
	}

	protected void handle(World world) {
		if (!handling)
			Tasks.wait(1, () -> calculate(world));
	}

	protected void handle(HasPlayer player) {
		handle(player.getPlayer().getWorld());
	}

	@EventHandler
	public void onBedEnter(PlayerBedEnterEvent event) {
		handle(event);
	}

	@EventHandler
	public void onBedLeave(PlayerBedLeaveEvent event) {
		handle(event);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		handle(event);
	}

	@EventHandler
	public void onPlayerVanish(VanishEvent event) {
		handle(event);
	}

	@EventHandler
	public void onPlayerChangeWorlds(PlayerChangedWorldEvent event) {
		handle(event);
	}

	@EventHandler
	public void onGameModeChange(PlayerGameModeChangeEvent event) {
		handle(event);
	}
}
