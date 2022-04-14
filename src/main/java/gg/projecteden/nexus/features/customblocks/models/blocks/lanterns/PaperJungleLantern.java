package gg.projecteden.nexus.features.customblocks.models.blocks.lanterns;

import gg.projecteden.nexus.features.customblocks.models.interfaces.ICustomBlock;
import gg.projecteden.nexus.features.customblocks.models.interfaces.ISidewaysBlock;
import lombok.NonNull;
import org.bukkit.Instrument;

public class PaperJungleLantern implements ICustomBlock, ISidewaysBlock {
	@Override
	public @NonNull Instrument getNoteBlockInstrument() {
		return Instrument.FLUTE;
	}

	@Override
	public int getNoteBlockStep() {
		return 10;
	}

	@Override
	public @NonNull String getName() {
		return "Paper Jungle Lantern";
	}

	@Override
	public int getCustomModelData() {
		return 20404;
	}

	@Override
	public @NonNull Instrument getNoteBlockInstrument_NS() {
		return getNoteBlockInstrument();
	}

	@Override
	public int getNoteBlockStep_NS() {
		return 11;
	}

	@Override
	public @NonNull Instrument getNoteBlockInstrument_EW() {
		return getNoteBlockInstrument();
	}

	@Override
	public int getNoteBlockStep_EW() {
		return 12;
	}
}
