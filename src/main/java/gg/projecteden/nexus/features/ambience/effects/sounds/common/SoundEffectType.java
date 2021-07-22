package gg.projecteden.nexus.features.ambience.effects.sounds.common;

import gg.projecteden.nexus.features.ambience.effects.common.ConditionalEffect;
import gg.projecteden.nexus.models.ambience.AmbienceUser;
import lombok.AllArgsConstructor;

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
