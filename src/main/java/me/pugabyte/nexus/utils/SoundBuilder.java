package me.pugabyte.nexus.utils;

import lombok.Data;
import lombok.NoArgsConstructor;
import me.lexikiq.HasPlayer;
import me.pugabyte.nexus.features.particles.MathUtils;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
public class SoundBuilder implements Cloneable {
	private List<HasPlayer> receivers;
	private Location location;
	private Sound sound;
	private SoundCategory category = SoundCategory.MASTER;
	private float volume = 0.5F;
	private float pitch = 1F;
	private int delay = 0;

	public SoundBuilder(Sound sound) {
		this.sound = sound;
	}

	public SoundBuilder sound(Sound sound) {
		this.sound = sound;
		return this;
	}

	public SoundBuilder reciever(HasPlayer reciever) {
		this.receivers = Collections.singletonList(reciever);
		return this;
	}

	public SoundBuilder recievers(List<HasPlayer> recievers) {
		this.receivers = recievers;
		return this;
	}

	public SoundBuilder location(Location location) {
		this.location = location;
		return this;
	}

	public SoundBuilder location(Block block) {
		this.location = block.getLocation();
		return this;
	}

	public SoundBuilder volume(double volume) {
		return volume((float) volume);
	}

	public SoundBuilder volume(float volume) {
		volume = Math.max(volume, 0.0F);
		this.volume = volume;
		return this;
	}

	public SoundBuilder pitch(double pitch) {
		return pitch((float) pitch);
	}

	public SoundBuilder pitchStep(int step) {
		return pitch(SoundUtils.getPitch(step));
	}

	public SoundBuilder pitch(float pitch) {
		pitch = MathUtils.clamp(pitch, 0.1F, 2.0F);
		this.pitch = pitch;
		return this;
	}

	public SoundBuilder category(SoundCategory category) {
		this.category = category;
		return this;
	}

	public SoundBuilder delay(int delay) {
		this.delay = delay;
		return this;
	}

	public SoundBuilder clone() {
		SoundBuilder soundBuilder = new SoundBuilder(this.sound);
		soundBuilder.recievers(new ArrayList<>(this.receivers));
		soundBuilder.location(this.location.clone());
		soundBuilder.category(this.category);
		soundBuilder.pitch(this.pitch);
		soundBuilder.volume(this.volume);
		soundBuilder.delay(this.delay);
		return soundBuilder;
	}

	public void play() {
		if (sound == null)
			throw new InvalidInputException("SoundBuilder: Sound cannot be null!");

		if (Utils.isNullOrEmpty(receivers) && location != null)
			// play sound in world
			Tasks.wait(delay, () -> location.getWorld().playSound(location, sound, category, volume, pitch));

		else {
			// Play sound to receivers
			for (HasPlayer receiver : receivers) {
				if (location != null && receiver.getPlayer().getWorld() != location.getWorld())
					continue;

				Tasks.wait(delay, () -> {
					Location origin = location;
					if (origin == null)
						origin = receiver.getPlayer().getLocation();

					Location finalOrigin = origin;
					receiver.getPlayer().playSound(finalOrigin, sound, category, volume, pitch);
				});
			}
		}
	}
}
