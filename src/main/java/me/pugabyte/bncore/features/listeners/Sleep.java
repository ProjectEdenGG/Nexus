package me.pugabyte.bncore.features.listeners;

import me.pugabyte.bncore.features.afk.AFK;
import me.pugabyte.bncore.features.store.perks.joinquit.VanishEvent;
import me.pugabyte.bncore.utils.ActionBarUtils;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

import static me.pugabyte.bncore.utils.StringUtils.colorize;

public class Sleep implements Listener {
	private static final String PREFIX = StringUtils.getPrefix("Sleep");
	public boolean handling = false;
	public long lastCalculatedSleeping = 0;
	public long lastCalculatedNeeded = 0;

	public void calculate(World world) {
		if (!(world.getTime() >= 12541 && world.getTime() <= 23458))
			return;

		Boolean gameRuleValue = world.getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE);
		if (gameRuleValue != null && !gameRuleValue)
			return;

		List<Player> players = world.getPlayers();
		long sleeping = players.stream().filter(Player::isSleeping).count();
		long active = players.stream().filter(player -> !Utils.isVanished(player) && !AFK.get(player).isAfk()).count();

		if (sleeping == 0)
			return;

		int needed = (int) Math.ceil(active / 2);

		if (sleeping != lastCalculatedSleeping || needed != lastCalculatedNeeded)
			for (Player player : players)
				ActionBarUtils.sendActionBar(player, "Sleepers needed to skip night: &e" + sleeping + "&3/&e" + needed);

		lastCalculatedSleeping = sleeping;
		lastCalculatedNeeded = needed;

		if (sleeping >= needed) {
			handling = true;
			Tasks.wait(20, () -> {
				players.forEach(player -> player.sendMessage(colorize(PREFIX + "The night was skipped because 50% of players slept!")));
				world.setTime(0);
				world.setStorm(false);
				world.setThundering(false);

				handling = false;
				lastCalculatedSleeping = 0;
				lastCalculatedNeeded = 0;
			});
		}
	}

	@EventHandler
	public void onBedEnter(PlayerBedEnterEvent event) {
		if (!handling)
			Tasks.wait(1, () -> calculate(event.getPlayer().getWorld()));
	}

	@EventHandler
	public void onBedLeave(PlayerBedLeaveEvent event) {
		if (!handling)
			Tasks.wait(1, () -> calculate(event.getPlayer().getWorld()));
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (!handling)
			Tasks.wait(1, () -> calculate(event.getPlayer().getWorld()));
	}

	@EventHandler
	public void onPlayerVanish(VanishEvent event) {
		if (!handling)
			Tasks.wait(1, () -> calculate(event.getPlayer().getWorld()));
	}

	@EventHandler
	public void onPlayerChangeWorlds(PlayerChangedWorldEvent event) {
		if (!handling)
			Tasks.wait(1, () -> calculate(event.getPlayer().getWorld()));
	}
}
