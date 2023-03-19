package gg.projecteden.nexus.features.resourcepack.decoration.common;

import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import org.bukkit.Location;

public interface NoiseMaker {
	String getSound();

	default void playSound(Location location) {
		String sound = getSound();
		if (sound == null)
			return;

		new SoundBuilder(sound).pitch(RandomUtils.randomDouble(0.1, 2)).location(location).play();
	}
}
