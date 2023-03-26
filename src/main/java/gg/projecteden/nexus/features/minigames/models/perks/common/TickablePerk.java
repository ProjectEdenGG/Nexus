package gg.projecteden.nexus.features.minigames.models.perks.common;

import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.perks.Perk;
import gg.projecteden.nexus.features.vanish.Vanish;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public interface TickablePerk extends Perk {
	default void tick(Minigamer minigamer) {
		tick(minigamer.getOnlinePlayer());
	}

	void tick(Player player);

	default boolean shouldTickFor(Player player) {
		final Minigamer minigamer = Minigamer.of(player);

		if (minigamer.isPlaying() && (minigamer.isRespawning() || !minigamer.usesPerk(this)))
			return false;
		if (Vanish.isVanished(player))
			return false;
		if (player.getGameMode() == GameMode.SPECTATOR)
			return false;
		if (!minigamer.isAlive())
			return false;

		if (this instanceof PlayerParticlePerk)
			if (minigamer.isPlaying())
				if (!minigamer.getMatch().getMechanic().shouldTickParticlePerks())
					return false;

		return true;
	}
}
