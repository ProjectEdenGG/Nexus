package gg.projecteden.nexus.features.customblocks.models.blocks.lanterns;

import gg.projecteden.nexus.features.customblocks.models.interfaces.ICustomBlock;
import gg.projecteden.nexus.features.customblocks.models.interfaces.ISidewaysBlock;
import lombok.NonNull;
import org.bukkit.Instrument;

public class PaperAcaciaLantern implements ICustomBlock, ISidewaysBlock {
	@Override
	public @NonNull Instrument getNoteBlockInstrument() {
		return Instrument.FLUTE;
	}

	@Override
	public int getNoteBlockStep() {
		return 13;
	}

	@Override
	public @NonNull String getName() {
		return "Paper Acacia Lantern";
	}

	@Override
	public int getCustomModelData() {
		return 20405;
	}

	@Override
	public @NonNull Instrument getNoteBlockInstrument_NS() {
		return getNoteBlockInstrument();
	}

	@Override
	public int getNoteBlockStep_NS() {
		return 14;
	}

	@Override
	public @NonNull Instrument getNoteBlockInstrument_EW() {
		return getNoteBlockInstrument();
	}

	@Override
	public int getNoteBlockStep_EW() {
		return 15;
	}
}
