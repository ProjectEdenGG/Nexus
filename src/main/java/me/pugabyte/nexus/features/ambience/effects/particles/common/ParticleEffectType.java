package me.pugabyte.nexus.features.ambience.effects.particles.common;

import lombok.AllArgsConstructor;
import me.pugabyte.nexus.models.ambience.AmbienceUser;
import me.pugabyte.nexus.models.ambience.Variables.TimeQuadrant;
import me.pugabyte.nexus.utils.BiomeTag;
import me.pugabyte.nexus.utils.RandomUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

@AllArgsConstructor
public enum ParticleEffectType implements ConditionalParticleEffect {
	FALLING_LEAVES {
		@Override
		public boolean conditionsMet(AmbienceUser user, ParticleEffectConfig config, Block block) {
			Player player = user.getPlayer();
			if (player == null)
				return false;

			if (!isCorrectMaterial(config, block))
				return false;

			if (RandomUtils.chanceOf(config.getChance()))
				return false;

			return true;
		}
	},
	DUST_WIND {
		@Override
		public boolean conditionsMet(AmbienceUser user, ParticleEffectConfig config, Block block) {
			Player player = user.getPlayer();
			if (player == null)
				return false;

			if (isUnderground(user))
				return false;

			if (!isWindBlowing())
				return false;

			if (!isBiome(user, BiomeTag.ALL_DESERT, BiomeTag.MESA))
				return false;

			if (!isCorrectMaterial(config, block))
				return false;

			if (RandomUtils.chanceOf(config.getChance()))
				return false;

			return true;
		}
	},
	FIREFLIES {
		@Override
		public boolean conditionsMet(AmbienceUser user, ParticleEffectConfig config, Block block) {
			Player player = user.getPlayer();
			if (player == null)
				return false;

			if (!isCorrectMaterial(config, block))
				return false;

			if (!isTimeQuadrant(user, TimeQuadrant.NIGHT))
				return false;

			if (isStorming(user))
				return false;

			if (RandomUtils.chanceOf(config.getChance()))
				return false;

			return true;
		}
	},
	;

	abstract public boolean conditionsMet(AmbienceUser user, ParticleEffectConfig config, Block block);
}
