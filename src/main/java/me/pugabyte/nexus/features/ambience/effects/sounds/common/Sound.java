package me.pugabyte.nexus.features.ambience.effects.sounds.common;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Sound {
	private String name;
	private double volumeMin = 1;
	private double volumeMax = 1;
	private double pitchMin = 1;
	private double pitchMax = 1;
	private int delay = 0;
	private double probability = 1;

	public Sound(String name) {
		this.name = name;
	}

	public Sound sound(String name) {
		this.name = name;
		return this;
	}

	public Sound volume(double volume) {
		this.volumeMin = volume;
		this.volumeMax = volume;
		return this;
	}

	public Sound volumeMin(double volumeMin) {
		this.volumeMin = volumeMin;
		return this;
	}

	public Sound volumeMax(double volumeMax) {
		this.volumeMax = volumeMax;
		return this;
	}

	public Sound pitch(double pitch) {
		this.pitchMin = pitch;
		this.pitchMax = pitch;
		return this;
	}

	public Sound pitchMin(double pitchMin) {
		this.pitchMin = pitchMin;
		return this;
	}

	public Sound pitchMax(double pitchMax) {
		this.pitchMax = pitchMax;
		return this;
	}

	public Sound delay(int delay) {
		this.delay = delay;
		return this;
	}

	public Sound probability(double probability) {
		this.probability = probability;
		return this;
	}

	public double getRandomVolume() {
		return volumeMin + (Math.random() * (volumeMax - volumeMin));
	}

	public double getRandomPitch() {
		return pitchMin + (Math.random() * (pitchMax - pitchMin));
	}
}
