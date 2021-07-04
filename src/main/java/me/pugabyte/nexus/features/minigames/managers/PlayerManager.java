package me.pugabyte.nexus.features.minigames.managers;

import me.lexikiq.HasUniqueId;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.framework.exceptions.postconfigured.PlayerNotOnlineException;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;

import java.util.UUID;

public class PlayerManager {

	public static Minigamer get(UUID player) {
		for (Match match : MatchManager.getAll()) {
			for (Minigamer minigamer : match.getMinigamers()) {
				if (minigamer.getUniqueId().equals(player)) {
					return minigamer;
				}
			}
		}

		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player);
		Player onlinePlayer = offlinePlayer.getPlayer();
		if (onlinePlayer == null)
			throw new PlayerNotOnlineException(offlinePlayer);
		return new Minigamer(player);
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
