package me.pugabyte.bncore.features.listeners;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.afk.AFK;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static me.pugabyte.bncore.BNCore.sleep;
import static me.pugabyte.bncore.utils.StringUtils.colorize;

public class Sleep implements Listener {
	private final String PREFIX = StringUtils.getPrefix("Sleep");
	public boolean handling = false;

	public Sleep() {
		BNCore.registerListener(this);
	}

	public void calculate(World world) {
		Collection<? extends Player> players = Bukkit.getOnlinePlayers().stream()
				.filter(player -> player.getWorld().equals(world))
				.collect(Collectors.toList());
		List<Player> sleepers = new ArrayList<>();
		int total = players.size();

		int sleeping = 0;
		for (Player player : players) {
			if (player.isSleeping()) {
				sleepers.add(player);
				sleeping++;
			} else if (Utils.isVanished(player)) {
				sleeping++;
			} else if (AFK.get(player).isAfk()) {
				sleeping++;
			}
		}

		if (sleeping == 0) {
			return;
		}

		int needed = (int) Math.ceil((double) players.size() / 2);
		sleepers.forEach(player -> player.sendMessage(colorize(PREFIX + "Sleepers needed: &e" + sleepers.size() + "&3/&e" + needed)));

		int percentage = (int) (((double) sleeping / total) * 100);

		if (percentage >= 49) {
			setHandling(true);
			Tasks.wait(20, () -> {
				players.forEach(player -> player.sendMessage(colorize(PREFIX + "The night was skipped because 50% of players slept!")));
				world.setTime(0);
				world.setStorm(false);
				world.setThundering(false);

				setHandling(false);
			});
		}
	}

	private void setHandling(boolean handling) {
		this.handling = handling;
	}

	boolean isHandling() {
		return handling;
	}

	@EventHandler
	public void onBedEnter(PlayerBedEnterEvent event) {
		if (!sleep.isHandling()) {
			Tasks.wait(1, () -> sleep.calculate(event.getPlayer().getWorld()));
		}
	}

	@EventHandler
	public void onBedLeave(PlayerBedLeaveEvent event) {
		if (!sleep.isHandling()) {
			World world = event.getPlayer().getWorld();
			if (world.getTime() >= 12541 && world.getTime() <= 23458) {
				Tasks.wait(1, () -> sleep.calculate(world));
			}
		}
	}
}
