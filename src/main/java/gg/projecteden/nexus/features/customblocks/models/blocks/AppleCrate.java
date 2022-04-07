package gg.projecteden.nexus.features.customblocks.models.blocks;

import gg.projecteden.nexus.features.customblocks.models.ICustomBlock;
import lombok.NonNull;
import org.bukkit.Instrument;

public class AppleCrate implements ICustomBlock {
	@Override
	public @NonNull Instrument getNoteBlockInstrument() {
		return Instrument.BASS_DRUM;
	}

	@Override
	public int getNoteBlockStep() {
		return 1;
	}

	@Override
	public String getName() {
		return "Crate of Apples";
	}

	@Override
	public int getCustomModelData() {
		return 20051;
	}
}
