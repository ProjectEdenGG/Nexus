package gg.projecteden.nexus.features.customblocks.models.blocks.stones;

import gg.projecteden.nexus.features.customblocks.models.interfaces.ICustomBlock;
import lombok.NonNull;
import org.bukkit.Instrument;

public class ChiseledAndesite implements ICustomBlock {
	@Override
	public @NonNull Instrument getNoteBlockInstrument() {
		return Instrument.DIDGERIDOO;
	}

	@Override
	public int getNoteBlockStep() {
		return 3;
	}

	@Override
	public @NonNull String getName() {
		return "Chiseled Andesite";
	}

	@Override
	public int getCustomModelData() {
		return 20352;
	}
}
