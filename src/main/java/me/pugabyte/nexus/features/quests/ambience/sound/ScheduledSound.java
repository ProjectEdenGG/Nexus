package me.pugabyte.nexus.features.quests.ambience.sound;

import lombok.Data;

@Data
public class ScheduledSound {
	private final String name;
	private final double x;
	private final double y;
	private final double z;
	private final float volume;
	private final float pitch;

	private final int delay;
	private final long startTime;

	public ScheduledSound(String name, double x, double y, double z, float volume, float pitch, int delay) {
		this.name = name;
		this.x = x;
		this.y = y;
		this.z = z;
		this.volume = volume;
		this.pitch = pitch;

		this.delay = delay;
		this.startTime = System.currentTimeMillis();
	}
}
