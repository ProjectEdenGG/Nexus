package gg.projecteden.nexus.features.customblocks.models.blocks.quilted_wool;

import gg.projecteden.nexus.features.customblocks.models.interfaces.ICustomBlock;
import lombok.NonNull;
import org.bukkit.Instrument;

public class MagentaQuiltedWool implements ICustomBlock {
	@Override
	public @NonNull Instrument getNoteBlockInstrument() {
		return Instrument.COW_BELL;
	}

	@Override
	public int getNoteBlockStep() {
		return 10;
	}

	@Override
	public @NonNull String getName() {
		return "Magenta Quilted Wool";
	}

	@Override
	public int getCustomModelData() {
		return 20310;
	}
}
