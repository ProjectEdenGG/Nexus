package me.pugabyte.nexus.features.minigames.models.perks.common;

import me.pugabyte.nexus.features.minigames.models.perks.IHasPerkCategory;
import me.pugabyte.nexus.features.minigames.models.perks.Perk;
import org.bukkit.Particle;

public interface IParticlePerk extends IHasPerkCategory, Perk {
	int getCount();

	Particle getParticle();

	default double getSpeed() {
		return 0.01d;
	}

	double getOffsetH();
	double getOffsetV();
}
