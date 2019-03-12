package me.pugabyte.bncore.features.sleep;

import me.pugabyte.bncore.BNCore;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Collection;

public class Sleep {
	public boolean handling;

	public Sleep() {
		new SleepListener();
	}

	public void calculate(World world) {
		Collection<? extends Player> players = Bukkit.getOnlinePlayers();
		int total = players.size();

		int sleeping = 0;
		for (Player player : players) {
			if (player.isSleeping()) {
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

		int percentage = ((sleeping / total) * 100);

		if (percentage >= 49) {
			setHandling(true);
			BNCore.runTaskLater(() -> {
				world.setTime(0);

				world.setStorm(false);
				world.setThundering(false);

				setHandling(false);
			}, 20);

		}
	}

	private void setHandling(boolean handling) {
		this.handling = handling;
	}

	boolean isHandling() {
		return handling;
	}
}
