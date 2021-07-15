package me.pugabyte.nexus.features.ambience.effects.sounds.common.general;

import lombok.AllArgsConstructor;
import me.pugabyte.nexus.features.ambience.effects.common.ConditionalEffect;
import me.pugabyte.nexus.models.ambience.AmbienceUser;
import me.pugabyte.nexus.utils.BiomeTag;
import org.bukkit.World.Environment;

@AllArgsConstructor
public enum SoundEffectType implements ConditionalEffect {
	BIRD_FOREST {
		@Override
		public boolean conditionsMet(AmbienceUser user, SoundEffectConfig config) {
			if (isUnderground(user) || isSubmerged(user) || isRaining(user) || isThundering(user))
				return false;

			if (!isDimension(user, Environment.NORMAL))
				return false;

			if (isBiome(user, BiomeTag.JUNGLE, BiomeTag.SWAMP))
				return false;

			return true;
		}
	},
	;

	abstract public boolean conditionsMet(AmbienceUser user, SoundEffectConfig config);
}
