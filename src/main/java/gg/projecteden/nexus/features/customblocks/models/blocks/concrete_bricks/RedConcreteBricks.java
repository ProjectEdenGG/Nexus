package gg.projecteden.nexus.features.customblocks.models.blocks.concrete_bricks;

import gg.projecteden.nexus.features.customblocks.models.interfaces.ICustomBlock;
import lombok.NonNull;
import org.bukkit.Instrument;

public class RedConcreteBricks implements ICustomBlock {
	@Override
	public @NonNull Instrument getNoteBlockInstrument() {
		return Instrument.CHIME;
	}

	@Override
	public int getNoteBlockStep() {
		return 1;
	}

	@Override
	public @NonNull String getName() {
		return "Red Concrete Bricks";
	}

	@Override
	public int getCustomModelData() {
		return 20251;
	}
}
