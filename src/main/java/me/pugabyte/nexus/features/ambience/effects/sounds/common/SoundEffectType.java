package me.pugabyte.nexus.features.ambience.effects.sounds.common;

import lombok.AllArgsConstructor;
import me.pugabyte.nexus.features.ambience.effects.common.ConditionalEffect;
import me.pugabyte.nexus.models.ambience.AmbienceUser;

@AllArgsConstructor
public enum SoundEffectType implements ConditionalEffect {
	THING {
		@Override
		public boolean conditionsMet(AmbienceUser user, SoundEffectConfig config) {
			return false;
		}
	};

	abstract public boolean conditionsMet(AmbienceUser user, SoundEffectConfig config);
}
