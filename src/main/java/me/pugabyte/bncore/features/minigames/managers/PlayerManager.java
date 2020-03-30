package me.pugabyte.bncore.features.minigames.managers;

import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import org.bukkit.entity.Player;

public class PlayerManager {

	public static Minigamer get(Player player) {
		for (Match match : MatchManager.getAll()) {
			for (Minigamer minigamer : match.getMinigamers()) {
				if (minigamer.getPlayer().equals(player)) {
					return minigamer;
				}
			}
		}

		return new Minigamer(player);
	}

}
