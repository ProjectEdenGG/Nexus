package me.pugabyte.nexus.features.minigames.models.perks.common;

import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.perks.Perk;
import org.bukkit.entity.Player;

public abstract class TickablePerk extends Perk {
	public void tick(Minigamer minigamer) {
		tick(minigamer.getPlayer());
	}

	public abstract void tick(Player player);
}
