package gg.projecteden.nexus.features.ambience.effects.particles.common;

import gg.projecteden.nexus.models.ambience.AmbienceUser;
import gg.projecteden.nexus.models.ambience.Variables.TimeQuadrant;
import gg.projecteden.nexus.utils.BiomeTag;
import gg.projecteden.nexus.utils.RandomUtils;
import lombok.AllArgsConstructor;
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

			return !RandomUtils.chanceOf(config.getChance());
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

			return !RandomUtils.chanceOf(config.getChance());
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

			if (!isBiome(user, BiomeTag.ALL_FORESTS, BiomeTag.JUNGLE, BiomeTag.PLAINS))
				return false;

			return !RandomUtils.chanceOf(config.getChance());
		}
	},
	;

	abstract public boolean conditionsMet(AmbienceUser user, ParticleEffectConfig config, Block block);
}
