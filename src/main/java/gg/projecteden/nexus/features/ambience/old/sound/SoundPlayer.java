package gg.projecteden.nexus.features.ambience.old.sound;

import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.Utils;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;

public class SoundPlayer {
	Player player;
	private final List<ScheduledSound> scheduledSounds = new LinkedList<>();

	public void update() {
		// process scheduled sounds
		long time = System.currentTimeMillis();
		Utils.removeIf(scheduledSound -> time - scheduledSound.getStartTime() >= scheduledSound.getDelay(), this::playSound, scheduledSounds);
	}

	public void playSound(Sound sound, double x, double y, double z) {
		// check sound probability
		if (sound.getProbability() < 1 && Math.random() >= sound.getProbability()) return;

		// calculate volume and pitch
		float volume = sound.getVolume();
		float pitch = sound.getPitch();

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

	public void playSound(String sound, double x, double y, double z, float volume, float pitch) {
		playSound(sound, new Location(player.getWorld(), x, y, z), volume, pitch);
	}

	public void playSound(String sound, float volume, float pitch) {
		playSound(sound, player.getLocation(), volume, pitch);
	}

	public void playSound(String sound, Location location, float volume, float pitch) {
		new SoundBuilder(org.bukkit.Sound.valueOf(sound))
			.receiver(player)
			.location(location)
			.category(SoundCategory.AMBIENT)
			.volume(volume)
			.pitch(pitch)
			.play();
	}

	public void stopSounds() {
		// TODO
	}
}
