package gg.projecteden.nexus.features.customblocks.models.blocks.concrete_bricks;

import gg.projecteden.nexus.features.customblocks.models.interfaces.ICustomBlock;
import lombok.NonNull;
import org.bukkit.Instrument;

public class LightGrayConcreteBricks implements ICustomBlock {
	@Override
	public @NonNull Instrument getNoteBlockInstrument() {
		return Instrument.CHIME;
	}

	@Override
	public int getNoteBlockStep() {
		return 15;
	}

	@Override
	public @NonNull String getName() {
		return "Light Gray Concrete Bricks";
	}

	@Override
	public int getCustomModelData() {
		return 20265;
	}
}
