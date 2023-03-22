package gg.projecteden.nexus.features.resourcepack.decoration.types.instruments;

import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.types.instruments.Instrument.InstrumentSound;
import gg.projecteden.nexus.utils.SoundBuilder;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public interface NoiseMaker {
	@Nullable Instrument.InstrumentSound getInstrumentSound();

	default double playSound(Player debugger, Location location, @NonNull InstrumentSound instrumentSound, double lastPitch) {
		double pitch = getPitch(lastPitch);

		new SoundBuilder(instrumentSound.getSound())
			.pitch(pitch)
			.category(SoundCategory.RECORDS)
			.location(location)
			.play();

		DecorationUtils.debug(debugger, "Pitch: " + pitch);
		return pitch;
	}

	double getPitch(double lastPitch);
}
