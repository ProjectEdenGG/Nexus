package me.pugabyte.nexus.features.ambience.effects.particles.common;

import lombok.AllArgsConstructor;
import me.pugabyte.nexus.features.ambience.effects.common.ConditionalEffect;
import me.pugabyte.nexus.models.ambience.AmbienceUser;
import me.pugabyte.nexus.models.ambience.Variables.TimeQuadrant;
import me.pugabyte.nexus.utils.BiomeTag;
import me.pugabyte.nexus.utils.RandomUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

@AllArgsConstructor
public enum ParticleEffectType implements ConditionalEffect {
	FALLING_LEAVES {
		@Override
		public boolean conditionsMet(AmbienceUser user, ParticleEffectConfig config, Block block, double x, double y, double z) {
			Player player = user.getPlayer();
			if (player == null)
				return false;

			if(!isSpawnMaterial(config, block) || !isAboveMaterial(config, block))
				return false;

			return true;
		}
	},
	DUST_WIND {
		@Override
		public boolean conditionsMet(AmbienceUser user, ParticleEffectConfig config, Block block, double x, double y, double z) {
			Player player = user.getPlayer();
			if (player == null)
				return false;

			if(RandomUtils.chanceOf(config.getChance()))
				return false;

			if(!isWindBlowing() || !isBiome(user, BiomeTag.ALL_DESERT, BiomeTag.MESA))
				return false;

			if(!isSpawnMaterial(config, block) || !isAboveMaterial(config, block))
				return false;

			if(!blockAbove(block, Material.AIR))
				return false;

			return true;
		}
	},
	FIREFLIES {
		@Override
		public boolean conditionsMet(AmbienceUser user, ParticleEffectConfig config, Block block, double x, double y, double z) {
			Player player = user.getPlayer();
			if (player == null)
				return false;

			if (!config.getSpawnMaterial().equals(block.getType()) || !config.getAboveMaterial().equals(block.getRelative(0, 1, 0).getType()))
				return false;

			if (!TimeQuadrant.NIGHT.equals(user.getVariables().getTimeQuadrant()))
				return false;

			return true;
		}
	},
	;


	abstract public boolean conditionsMet(AmbienceUser user, ParticleEffectConfig config, Block block, double x, double y, double z);
}
