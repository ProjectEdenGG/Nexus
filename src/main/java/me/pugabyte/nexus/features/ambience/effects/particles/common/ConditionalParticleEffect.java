package me.pugabyte.nexus.features.ambience.effects.particles.common;

import me.pugabyte.nexus.features.ambience.effects.common.ConditionalEffect;
import org.bukkit.block.Block;

public interface ConditionalParticleEffect extends ConditionalEffect {

	default boolean isCorrectMaterial(ParticleEffectConfig config, Block block) {
		if (config.getSpawnMaterial() != null && !isSpawnMaterial(config, block))
			return false;
		if (config.getAboveMaterial() != null && !isAboveMaterial(config, block))
			return false;
		if (config.getBelowMaterial() != null && !isBelowMaterial(config, block))
			return false;

		return true;
	}

	default boolean isSpawnMaterial(ParticleEffectConfig config, Block block) {
		return config.getSpawnMaterial().equals(block.getType());
	}

	default boolean isAboveMaterial(ParticleEffectConfig config, Block block) {
		return config.getAboveMaterial().equals(block.getRelative(0, 1, 0).getType());
	}

	default boolean isBelowMaterial(ParticleEffectConfig config, Block block) {
		return config.getBelowMaterial().equals(block.getRelative(0, -1, 0).getType());
	}

}
