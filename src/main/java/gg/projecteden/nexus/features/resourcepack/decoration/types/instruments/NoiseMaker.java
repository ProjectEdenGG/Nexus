package gg.projecteden.nexus.features.resourcepack.decoration.types.instruments;

import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationInteractEvent;
import gg.projecteden.nexus.utils.MathUtils;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.Utils;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicReference;

public interface NoiseMaker {
	String getSound();

	default double playSound(DecorationInteractEvent event, double lastPitch) {
		String sound = getSound();
		if (sound == null)
			return 1;

		Player debugger = event.getPlayer();
		Location location = event.getDecoration().getOrigin();

		AtomicReference<Double> pitch = new AtomicReference<>((double) 0);
		Utils.attempt(10, () -> {
			double newPitch = lastPitch + RandomUtils.randomDouble(-0.30, 0.30);
			newPitch = MathUtils.round(MathUtils.clamp(newPitch, 0.10, 2.00), 2);

			if (newPitch == lastPitch)
				return false;

			pitch.set(newPitch);
			return true;
		});

		new SoundBuilder(sound).pitch(pitch.get()).category(SoundCategory.RECORDS).location(location).play();

		DecorationUtils.debug(debugger, "Pitch: " + pitch.get());
		return pitch.get();
	}
}
