package me.pugabyte.nexus.features.minigames.managers;

import me.lexikiq.HasPlayer;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import org.bukkit.entity.Player;

public class PlayerManager {

	public static Minigamer get(HasPlayer player) {
		Player _player = player.getPlayer();
		for (Match match : MatchManager.getAll()) {
			for (Minigamer minigamer : match.getMinigamers()) {
				if (minigamer.getPlayer().equals(_player)) {
					return minigamer;
				}
			}
		}

		return new Minigamer(_player);
	}

}
