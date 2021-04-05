package me.pugabyte.nexus.features.minigames.models.perks.common;

import org.bukkit.Particle;

public interface IParticlePerk {
	default int getCount() {
		return 5;
	}

	Particle getParticle();

	default double getSpeed() {
		return 0.01d;
	}

	double getOffsetH();
	double getOffsetV();
}
