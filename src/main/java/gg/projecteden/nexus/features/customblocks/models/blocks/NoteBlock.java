package gg.projecteden.nexus.features.customblocks.models.blocks;

import gg.projecteden.nexus.features.customblocks.models.ICustomBlock;
import lombok.NonNull;
import org.bukkit.Instrument;

public class NoteBlock implements ICustomBlock {
	@Override
	public @NonNull Instrument getNoteBlockInstrument() {
		return Instrument.PIANO;
	}

	@Override
	public int getNoteBlockStep() {
		return 0;
	}

	@Override
	public String getName() {
		return "Note Block";
	}

	@Override
	public int getCustomModelData() {
		return 20000;
	}
}
