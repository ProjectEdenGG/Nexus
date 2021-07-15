package me.pugabyte.nexus.features.ambience.effects.sounds.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.models.ambience.AmbienceUser;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SoundEffect {
	private AmbienceUser user;
	private SoundEffectType effectType;
	private double volume;
	private double pitchMin;
	private double pitchMax;
}
