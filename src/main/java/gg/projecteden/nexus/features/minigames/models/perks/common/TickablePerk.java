package gg.projecteden.nexus.features.minigames.models.perks.common;

import gg.projecteden.nexus.features.minigames.managers.PlayerManager;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.perks.Perk;
import gg.projecteden.nexus.utils.PlayerUtils;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public interface TickablePerk extends Perk {
	default void tick(Minigamer minigamer) {
		tick(minigamer.getPlayer());
	}

	void tick(Player player);

	default boolean shouldTickFor(Player player) {
		final Minigamer minigamer = PlayerManager.get(player);

		if (minigamer.isPlaying() && (minigamer.isRespawning() || !minigamer.usesPerk(this)))
			return false;
		if (PlayerUtils.isVanished(player))
			return false;
		if (player.getGameMode() == GameMode.SPECTATOR)
			return false;
		if (!minigamer.isAlive())
			return false;

		return true;
	}
}
