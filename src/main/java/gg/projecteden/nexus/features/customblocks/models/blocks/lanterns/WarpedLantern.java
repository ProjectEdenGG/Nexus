package gg.projecteden.nexus.features.customblocks.models.blocks.lanterns;

import gg.projecteden.nexus.features.customblocks.models.interfaces.ICustomBlock;
import gg.projecteden.nexus.features.customblocks.models.interfaces.ISidewaysBlock;
import lombok.NonNull;
import org.bukkit.Instrument;

public class WarpedLantern implements ICustomBlock, ISidewaysBlock {
	@Override
	public @NonNull Instrument getNoteBlockInstrument() {
		return Instrument.FLUTE;
	}

	@Override
	public int getNoteBlockStep() {
		return 22;
	}

	@Override
	public @NonNull String getName() {
		return "Warped Lantern";
	}

	@Override
	public int getCustomModelData() {
		return 20408;
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
