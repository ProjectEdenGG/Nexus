package me.pugabyte.bncore.features.oldminigames;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.minigame.Minigame;
import me.pugabyte.bncore.features.oldminigames.murder.Murder;
import me.pugabyte.bncore.features.oldminigames.murder.MurderUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;

public class MinigameUtils {
	public static void resetExp(Player player) {
		if (player.getTotalExperience() != 0) {
			player.setLevel(0);
			player.setTotalExperience(0);
			player.setExp(0);
		}
	}

	public static void shufflePlayers(Minigame minigame) {
		shufflePlayers(minigame, false);
	}

	public static void shufflePlayers(Minigame minigame, boolean teleporter) {
		List<Location> locs = minigame.getStartLocations();
		Random rand = new Random();
		for (MinigamePlayer _minigamePlayer : minigame.getPlayers()) {
			boolean isMurder = minigame.getGametypeName().equalsIgnoreCase("Murder");
			boolean isMurderer = MurderUtils.isMurderer(_minigamePlayer.getPlayer());
			if (!isMurder || (!teleporter || !isMurderer)) {
				int n = rand.nextInt(locs.size());
				_minigamePlayer.setAllowTeleport(true);
				_minigamePlayer.getPlayer().teleport(locs.get(n));
				_minigamePlayer.setAllowTeleport(false);
				if (teleporter) {
					_minigamePlayer.sendMessage(Murder.PREFIX + "The murderer used the teleporter!");
				}
			}
		}
	}
}
