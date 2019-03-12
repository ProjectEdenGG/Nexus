package me.pugabyte.bncore.features.minigames.managers;

import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import org.bukkit.entity.Player;

import java.util.Optional;

import static me.pugabyte.bncore.features.minigames.Minigames.getMatchManager;

public class PlayerManager {
	public static Minigamer get(Player player) {
		Optional<Match> optionalMatch = getMatchManager().getAll().stream()
				.filter(match -> match.getMinigamers().stream()
						.anyMatch(minigamer -> minigamer.getPlayer().equals(player)))
				.findFirst();
		if (optionalMatch.isPresent()) {
			Optional<Minigamer> optionalMinigamer = optionalMatch.get().getMinigamers().stream()
					.filter(minigamer -> minigamer.getPlayer().equals(player)).findFirst();
			if (optionalMinigamer.isPresent()) {
				return optionalMinigamer.get();
			}
		}
		return new Minigamer(player);
	}
}
