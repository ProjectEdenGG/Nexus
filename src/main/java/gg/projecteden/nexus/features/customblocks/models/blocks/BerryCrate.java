package gg.projecteden.nexus.features.customblocks.models.blocks;

import gg.projecteden.nexus.features.customblocks.models.ICustomBlock;
import lombok.NonNull;
import org.bukkit.Instrument;

public class BerryCrate implements ICustomBlock {
	@Override
	public @NonNull Instrument getNoteBlockInstrument() {
		return Instrument.BASS_DRUM;
	}

	@Override
	public int getNoteBlockStep() {
		return 3;
	}

	@Override
	public String getName() {
		return "Crate of Berries";
	}

	@Override
	public int getCustomModelData() {
		return 20053;
	}
}
