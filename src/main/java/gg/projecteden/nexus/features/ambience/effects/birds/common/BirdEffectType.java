package gg.projecteden.nexus.features.ambience.effects.birds.common;

import gg.projecteden.nexus.features.ambience.effects.common.ConditionalEffect;
import gg.projecteden.nexus.models.ambience.AmbienceUser;
import gg.projecteden.nexus.utils.BiomeTag;
import lombok.AllArgsConstructor;
import org.bukkit.World.Environment;

@AllArgsConstructor
public enum BirdEffectType implements ConditionalEffect {
	FOREST {
		@Override
		public boolean conditionsMet2(AmbienceUser user, BirdEffectConfig config) {
			if (isBiome(user, BiomeTag.ALL_FORESTS))
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
