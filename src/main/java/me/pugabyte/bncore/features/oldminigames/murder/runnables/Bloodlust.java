package me.pugabyte.bncore.features.oldminigames.murder.runnables;

import au.com.mineauz.minigames.MinigamePlayer;
import me.pugabyte.bncore.skript.SkriptFunctions;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class Bloodlust extends BukkitRunnable {
	private List<MinigamePlayer> players;
	private int time;
	private double fadeTime = 0.5;
	private double intensity = 10;

	public Bloodlust(List<MinigamePlayer> players) {
		this.players = players;
		this.time = 10;
	}

	@Override
	public void run() {
		if (time > 0) {
			for (MinigamePlayer minigamePlayer : players) {
				Player player = minigamePlayer.getPlayer();
				player.playSound(player.getLocation(), "entity.player.breath", SoundCategory.MASTER, 2F, 0.1F);
				SkriptFunctions.redTint(player, fadeTime, intensity);
			}
			time--;
		} else {
			this.cancel();
		}
	}

}
