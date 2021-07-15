package me.pugabyte.nexus.features.ambience.effects.sounds.common;

import me.pugabyte.nexus.utils.SoundBuilder;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class SoundPlayer {
	Player player;
	private final List<ScheduledSound> scheduledSounds = new LinkedList<>();

	public void update() {
		// process scheduled sounds
		long time = System.currentTimeMillis();
		Iterator<ScheduledSound> iter = scheduledSounds.iterator();
		while (iter.hasNext()) {
			ScheduledSound scheduledSound = iter.next();
			if (time - scheduledSound.getStartTime() >= scheduledSound.getDelay()) {
				playSound(scheduledSound);
				iter.remove();
			}
		}
	}

	public void playSound(Sound sound, Location location) {
		playSound(sound, location.getX(), location.getY(), location.getZ());
	}

	public void playSound(Sound sound, double x, double y, double z) {
		// check sound probability
		if (sound.getProbability() < 1 && Math.random() >= sound.getProbability()) return;

		// calculate volume and pitch
		double volume = sound.getRandomVolume();
		double pitch = sound.getRandomPitch();

		// schedule if delay > 0
		if (sound.getDelay() > 0) {
			scheduledSounds.add(new ScheduledSound(sound.getName(), x, y, z, volume, pitch, sound.getDelay()));
		} else {
			playSound(sound.getName(), x, y, z, volume, pitch);
		}
	}

	public void playSound(ScheduledSound sound) {
		playSound(sound.getName(), sound.getX(), sound.getY(), sound.getZ(), sound.getVolume(), sound.getPitch());
	}

	public void playSound(String sound, double x, double y, double z, double volume, double pitch) {
		playSound(sound, new Location(player.getWorld(), x, y, z), volume, pitch);
	}

	public void playSound(String sound, double volume, double pitch) {
		playSound(sound, player.getLocation(), volume, pitch);
	}

	public void playSound(String sound, Location location, double volume, double pitch) {
		new SoundBuilder(org.bukkit.Sound.valueOf(sound))
			.receiver(player)
			.location(location)
			.category(SoundCategory.AMBIENT)
			.volume(volume)
			.pitch(pitch)
			.play();
	}
}
