package me.pugabyte.nexus.features.minigames.models.perks.common;

import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.perks.Perk;
import org.bukkit.entity.Player;

public interface TickablePerk extends Perk {
	default void tick(Minigamer minigamer) {
		tick(minigamer.getPlayer());
	}

	void tick(Player player);
}
