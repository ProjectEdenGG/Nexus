package me.pugabyte.nexus.features.ambience.effects.sounds.common;

import lombok.Data;

@Data
public class ScheduledSound {
	private final String name;
	private final double x;
	private final double y;
	private final double z;
	private final double volume;
	private final double pitch;

	private final int delay;
	private final long startTime;

	public ScheduledSound(String name, double x, double y, double z, double volume, double pitch, int delay) {
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
