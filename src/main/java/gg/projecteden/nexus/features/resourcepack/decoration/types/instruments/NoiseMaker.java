package gg.projecteden.nexus.features.resourcepack.decoration.types.instruments;

import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationInteractEvent;
import gg.projecteden.nexus.utils.SoundBuilder;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public interface NoiseMaker {
	@Nullable String getSound();

	default double playSound(DecorationInteractEvent event, double lastPitch) {
		String sound = getSound();
		if (sound == null)
			return 1;

		Player debugger = event.getPlayer();
		Location location = event.getDecoration().getOrigin();
		double pitch = getPitch(lastPitch);


		new SoundBuilder(sound).pitch(pitch).category(SoundCategory.RECORDS).location(location).play();

		DecorationUtils.debug(debugger, "Pitch: " + pitch);
		return pitch;
	}

	double getPitch(double lastPitch);
}
