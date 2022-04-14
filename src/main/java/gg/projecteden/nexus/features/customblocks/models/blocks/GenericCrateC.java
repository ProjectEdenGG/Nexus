package gg.projecteden.nexus.features.customblocks.models.blocks;

import gg.projecteden.nexus.features.customblocks.models.interfaces.ICustomBlock;
import lombok.NonNull;
import org.bukkit.Instrument;

public class GenericCrateC implements ICustomBlock {
	@Override
	public @NonNull Instrument getNoteBlockInstrument() {
		return Instrument.BASS_GUITAR;
	}

	@Override
	public int getNoteBlockStep() {
		return 3;
	}

	@Override
	public @NonNull String getName() {
		return "Generic Crate";
	}

	@Override
	public int getCustomModelData() {
		return 20103;
	}
}
