package me.pugabyte.nexus.features.ambience.effects.common;

import me.pugabyte.nexus.features.ambience.Wind;
import me.pugabyte.nexus.models.ambience.AmbienceUser;
import me.pugabyte.nexus.utils.BiomeTag;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public interface ConditionalEffect {

	default boolean isDimension(AmbienceUser user, Environment environment) {
		return user.getVariables().getDimension().equals(environment);
	}

	default boolean isBiome(AmbienceUser user, BiomeTag... biomeTags) {
		for (BiomeTag biomeTag : biomeTags) {
			if (biomeTag.isTagged(user.getVariables().getBiome()))
				return true;
		}

		return false;
	}

	default boolean isUnderground(AmbienceUser user) {
		return !user.getVariables().isExposed();
	}

	default boolean isSubmerged(AmbienceUser user) {
		return user.getVariables().isSubmerged();
	}

	default boolean isRaining(AmbienceUser user) {
		return user.getVariables().isRaining();
	}

	default boolean isThundering(AmbienceUser user) {
		return user.getVariables().isThundering();
	}

	default boolean isWindBlowing() {
		return Wind.isBlowing();
	}

	default boolean blockAboveIs(Block block, Material material) {
		return block.getRelative(BlockFace.UP).getType().equals(material);
	}

}
