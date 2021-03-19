package me.pugabyte.nexus.features.minigames.models.perks.common;

import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.perks.Perk;

public abstract class TickablePerk extends Perk {
	public abstract void tick(Minigamer minigamer);
}
