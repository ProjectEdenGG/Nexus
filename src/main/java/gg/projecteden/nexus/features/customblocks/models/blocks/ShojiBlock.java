package gg.projecteden.nexus.features.customblocks.models.blocks;

import gg.projecteden.nexus.features.customblocks.models.interfaces.ICustomBlock;
import gg.projecteden.nexus.features.customblocks.models.interfaces.ISidewaysBlock;
import lombok.NonNull;
import org.bukkit.Instrument;

public class ShojiBlock implements ICustomBlock, ISidewaysBlock {
	@Override
	public @NonNull Instrument getNoteBlockInstrument() {
		return Instrument.BASS_GUITAR;
	}

	@Override
	public int getNoteBlockStep() {
		return 22;
	}

	@Override
	public @NonNull String getName() {
		return "Shoji Block";
	}

	@Override
	public int getCustomModelData() {
		return 20122;
	}

	@Override
	public @NonNull Instrument getNoteBlockInstrument_NS() {
		return getNoteBlockInstrument();
	}

	@Override
	public int getNoteBlockStep_NS() {
		return 23;
	}

	@Override
	public @NonNull Instrument getNoteBlockInstrument_EW() {
		return getNoteBlockInstrument();
	}

	@Override
	public int getNoteBlockStep_EW() {
		return 24;
	}
}
