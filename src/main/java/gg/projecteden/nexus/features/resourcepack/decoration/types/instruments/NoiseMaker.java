package gg.projecteden.nexus.features.resourcepack.decoration.types.instruments;

import gg.projecteden.nexus.features.resourcepack.decoration.DecorationLang;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.Interactable;
import gg.projecteden.nexus.features.resourcepack.decoration.types.instruments.Instrument.InstrumentSound;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public interface NoiseMaker extends Interactable {
	@Nullable Instrument.InstrumentSound getInstrumentSound();

	default double playSound(Player debugger, Location location, @NonNull InstrumentSound instrumentSound, double lastPitch) {
		double pitch = getPitch(lastPitch);

		DecorationUtils.getSoundBuilder(instrumentSound.getSound())
				.pitch(pitch)
				.category(SoundCategory.RECORDS)
				.location(location)
				.play();

		DecorationLang.debug(debugger, "&ePitch: " + pitch);
		return pitch;
	}

	double getPitch(double lastPitch);
}
