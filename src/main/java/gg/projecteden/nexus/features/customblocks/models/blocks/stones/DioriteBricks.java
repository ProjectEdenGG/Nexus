package gg.projecteden.nexus.features.customblocks.models.blocks.stones;

import gg.projecteden.nexus.features.customblocks.models.interfaces.ICustomBlock;
import lombok.NonNull;
import org.bukkit.Instrument;

public class DioriteBricks implements ICustomBlock {
	@Override
	public @NonNull Instrument getNoteBlockInstrument() {
		return Instrument.DIDGERIDOO;
	}

	@Override
	public int getNoteBlockStep() {
		return 4;
	}

	@Override
	public @NonNull String getName() {
		return "Diorite Bricks";
	}

	@Override
	public int getCustomModelData() {
		return 20353;
	}
}
