package me.pugabyte.nexus.features.ambience.effects.birds.common;

import lombok.AllArgsConstructor;
import me.pugabyte.nexus.features.ambience.effects.common.ConditionalEffect;
import me.pugabyte.nexus.models.ambience.AmbienceUser;
import me.pugabyte.nexus.utils.BiomeTag;
import org.bukkit.World.Environment;

@AllArgsConstructor
public enum BirdEffectType implements ConditionalEffect {
	FOREST {
		@Override
		public boolean conditionsMet2(AmbienceUser user, BirdEffectConfig config) {
			if (isBiome(user, BiomeTag.FOREST))
				return false;

			return true;
		}
	},
	;

	public boolean conditionsMet(AmbienceUser user, BirdEffectConfig config) {
		if (isUnderground(user) || isSubmerged(user) || isRaining(user) || isThundering(user))
			return false;

		if (!isDimension(user, Environment.NORMAL))
			return false;

		return conditionsMet2(user, config);
	}

	// TODO Better name
	abstract public boolean conditionsMet2(AmbienceUser user, BirdEffectConfig config);
}
