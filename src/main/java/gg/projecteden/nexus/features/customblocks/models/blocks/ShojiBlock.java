package gg.projecteden.nexus.features.customblocks.models.blocks;

import gg.projecteden.nexus.features.customblocks.models.interfaces.ICustomBlock;
import gg.projecteden.nexus.features.customblocks.models.interfaces.ISidewaysBlock;
import lombok.NonNull;
import org.bukkit.Instrument;

public class ShojiBlock implements ICustomBlock, ISidewaysBlock {
	@Override
	public @NonNull Instrument getNoteBlockInstrument() {
		return null;
	}

	@Override
	public int getNoteBlockStep() {
		return 0;
	}

	@Override
	public @NonNull String getName() {
		return "ShojiBlock Block";
	}

	@Override
	public int getCustomModelData() {
		return 0;
	}

	@Override
	public @NonNull Instrument getNoteBlockInstrument_NS() {
		return null;
	}

	@Override
	public int getNoteBlockStep_NS() {
		return 0;
	}

	@Override
	public @NonNull Instrument getNoteBlockInstrument_EW() {
		return null;
	}

	@Override
	public int getNoteBlockStep_EW() {
		return 0;
	}
}
