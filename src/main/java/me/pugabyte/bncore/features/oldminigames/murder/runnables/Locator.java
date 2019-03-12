package me.pugabyte.bncore.features.oldminigames.murder.runnables;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.minigame.Minigame;
import me.pugabyte.bncore.BNCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class Locator {
	public static int run(Minigame minigame, Player player) {
		int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(BNCore.getInstance(), () -> {
			double dist = 1000;
			Player target = null;

			// Find the closest player by looping all minigame
			// players and saving the shortest distance
			MinigamePlayer mgPlayer = Minigames.getPlugin().getPlayerManager().getMinigamePlayer(player);
			List<MinigamePlayer> players = minigame.getPlayers();
			for (MinigamePlayer _i : players) {
				if (_i.getPlayer() != player) {
					if (player.getLocation().distance(_i.getPlayer().getLocation()) < dist) {
						// New shortest distance, save data
						dist = player.getLocation().distance(_i.getPlayer().getLocation());
						target = _i.getPlayer();
					}
				}
			}

			// Set compass location to nearest player
			player.setCompassTarget(target.getLocation());
		}, 2, 5); // Run every 5 ticks

		return taskId;
	}
}
