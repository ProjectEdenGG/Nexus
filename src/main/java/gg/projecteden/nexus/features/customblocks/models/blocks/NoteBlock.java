package gg.projecteden.nexus.features.customblocks.models.blocks;

import gg.projecteden.nexus.features.customblocks.models.interfaces.ICustomBlock;
import lombok.NonNull;
import org.bukkit.Instrument;
import org.jetbrains.annotations.NotNull;

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
	public @NotNull String getName() {
		return "Note Block";
	}

	@Override
	public int getCustomModelData() {
		return 20000;
	}
}
