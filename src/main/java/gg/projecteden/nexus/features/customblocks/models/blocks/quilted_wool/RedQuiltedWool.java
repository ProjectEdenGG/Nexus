package gg.projecteden.nexus.features.customblocks.models.blocks.quilted_wool;

import gg.projecteden.nexus.features.customblocks.models.interfaces.ICustomBlock;
import lombok.NonNull;
import org.bukkit.Instrument;

public class RedQuiltedWool implements ICustomBlock {
	@Override
	public @NonNull Instrument getNoteBlockInstrument() {
		return Instrument.COW_BELL;
	}

	@Override
	public int getNoteBlockStep() {
		return 1;
	}

	@Override
	public @NonNull String getName() {
		return "Red Quilted Wool";
	}

	@Override
	public int getCustomModelData() {
		return 20301;
	}
}
