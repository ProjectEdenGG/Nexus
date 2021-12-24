package gg.projecteden.nexus.features.ambience.effects.sounds.common;

import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.utils.Utils;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.SoundCategory;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Data
@RequiredArgsConstructor
public class SoundPlayer implements PlayerOwnedObject {
	@Getter
	private final UUID uuid;
	private final List<ScheduledSound> scheduledSounds = new LinkedList<>();

	public void update() {
		// process scheduled sounds
		long time = System.currentTimeMillis();
		Utils.removeIf(scheduledSound -> time - scheduledSound.getStartTime() >= scheduledSound.getDelay(), this::playSound, scheduledSounds);
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
		if (!isOnline())
			return;

		playSound(sound, new Location(getOnlinePlayer().getWorld(), x, y, z), volume, pitch);
	}

	public void playSound(String sound, double volume, double pitch) {
		playSound(sound, getOnlinePlayer().getLocation(), volume, pitch);
	}

	public void playSound(String sound, Location location, double volume, double pitch) {
		new SoundBuilder(sound)
			.receiver(getOnlinePlayer())
			.location(location)
			.category(SoundCategory.AMBIENT)
			.volume(volume)
			.pitch(pitch)
			.play();
	}

}
