package me.pugabyte.bncore.features.sleep;

import me.pugabyte.bncore.BNCore;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static me.pugabyte.bncore.BNCore.colorize;

public class Sleep {
	private final String PREFIX = BNCore.getPrefix("Sleep");
	public boolean handling = false;

	public Sleep() {
		new SleepListener();
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
			} else if (BNCore.isVanished(player)) {
				sleeping++;
			} else if (BNCore.isAfk(player)) {
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
			BNCore.wait(20, () -> {
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
}
