package gg.projecteden.nexus.features.noteblocks.blocks;

import gg.projecteden.nexus.features.noteblocks.ICustomBlock;
import lombok.NonNull;
import org.bukkit.Instrument;

public class AppleCrate implements ICustomBlock {
	@Override
	public @NonNull Instrument getNoteBlockInstrument() {
		return Instrument.BASS_DRUM;
	}

	@Override
	public int getNoteBlockStep() {
		return 5;
	}

	@Override
	public String getName() {
		return "Crate of Apples";
	}

	@Override
	public int getCustomModelData() {
		return 0; // TODO
	}
}
