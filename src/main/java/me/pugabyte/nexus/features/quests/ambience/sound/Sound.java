package me.pugabyte.nexus.features.quests.ambience.sound;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
class Sound {
	private final String name;
	private final float volumeMin;
	private final float volumeMax;
	private final float pitchMin;
	private final float pitchMax;

	private final int delay;
	private final double probability;

	public float getVolume() {
		return volumeMin + (float) (Math.random() * (volumeMax - volumeMin));
	}

	public float getPitch() {
		return pitchMin + (float) (Math.random() * (pitchMax - pitchMin));
	}
}
