package gg.projecteden.nexus.features.customblocks.models.blocks.quilted_wool;

import gg.projecteden.nexus.features.customblocks.models.interfaces.ICustomBlock;
import lombok.NonNull;
import org.bukkit.Instrument;

public class PurpleQuiltedWool implements ICustomBlock {
	@Override
	public @NonNull Instrument getNoteBlockInstrument() {
		return Instrument.COW_BELL;
	}

	@Override
	public int getNoteBlockStep() {
		return 9;
	}

	@Override
	public @NonNull String getName() {
		return "Purple Quilted Wool";
	}

	@Override
	public int getCustomModelData() {
		return 20309;
	}
}
