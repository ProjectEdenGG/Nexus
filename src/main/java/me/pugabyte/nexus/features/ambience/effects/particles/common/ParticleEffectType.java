package me.pugabyte.nexus.features.ambience.effects.particles.common;

import lombok.AllArgsConstructor;
import me.pugabyte.nexus.models.ambience.AmbienceUser;
import me.pugabyte.nexus.models.ambience.Variables.TimeQuadrant;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

@AllArgsConstructor
public enum ParticleEffectType {
	FALLING_LEAVES {
		@Override
		public boolean conditionsMet(ParticleEffectConfig config, AmbienceUser user, Block block, double x, double y, double z) {
			Player player = user.getPlayer();
			if (player == null)
				return false;

			if (!config.getSpawnMaterial().equals(block.getType()) || !config.getBelowMaterial().equals(block.getRelative(0, 1, 0).getType()))
				return false;

			return true;
		}
	},
	DUST_WIND {
		@Override
		public boolean conditionsMet(ParticleEffectConfig config, AmbienceUser user, Block block, double x, double y, double z) {
			Player player = user.getPlayer();
			if (player == null)
				return false;

			if (!config.getSpawnMaterial().equals(block.getType()) || !config.getAboveMaterial().equals(block.getRelative(0, 1, 0).getType()))
				return false;

			return true;

//			if(!RandomUtils.chanceOf(getChance())) return false;
//
//			Location location = player.getLocation();
//			Biome biome = location.getBlock().getBiome();
//
//			if(!Ambience.isWindBlowing()) return false;
//			if(!location.add(0, 1, 0).getBlock().getType().equals(Material.AIR)) return false;
//			if(!user.getVariables().isExposed()) return false;
//
//			return BiomeTag.ALL_DESERT.isTagged(biome) || BiomeTag.MESA.isTagged(biome);
		}
	},
	FIREFLIES {
		@Override
		public boolean conditionsMet(ParticleEffectConfig config, AmbienceUser user, Block block, double x, double y, double z) {
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

	abstract public boolean conditionsMet(ParticleEffectConfig config, AmbienceUser user, Block block, double x, double y, double z);
}
