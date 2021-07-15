package me.pugabyte.nexus.features.ambience.effects.sounds.common;

import lombok.AllArgsConstructor;
import me.pugabyte.nexus.models.ambience.AmbienceUser;

@AllArgsConstructor
public enum SoundEffectType {
	BIRD_FOREST {
		@Override
		public boolean conditionsMet(SoundEffectConfig config, AmbienceUser user) {
			return true;
		}
	},
	;

	abstract public boolean conditionsMet(SoundEffectConfig config, AmbienceUser user);
}
