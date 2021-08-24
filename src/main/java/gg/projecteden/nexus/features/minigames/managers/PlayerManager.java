package gg.projecteden.nexus.features.minigames.managers;

import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.framework.exceptions.postconfigured.PlayerNotOnlineException;
import me.lexikiq.HasUniqueId;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;

import java.util.UUID;

public class PlayerManager {

	public static Minigamer get(UUID uuid) {
		for (Match match : MatchManager.getAll())
			for (Minigamer minigamer : match.getMinigamers())
				if (minigamer.getUniqueId().equals(uuid))
					return minigamer;

		Player onlinePlayer = Bukkit.getPlayer(uuid);
		if (onlinePlayer == null)
			throw new PlayerNotOnlineException(uuid);
		return new Minigamer(uuid);
	}

	@Contract("null -> null; !null -> !null")
	public static Minigamer get(HasUniqueId player) {
		if (player == null) return null;
		if (player instanceof Minigamer minigamer)
			return minigamer;
		try {
			return get(player.getUniqueId());
		} catch (PlayerNotOnlineException exc) {
			// fake player (NPC), this should probably return null but to avoid breaking changes we create a fake minigamer as well
			if (player instanceof Player player1)
				return new Minigamer(player1.getUniqueId());
			throw exc;
		}
	}
}
