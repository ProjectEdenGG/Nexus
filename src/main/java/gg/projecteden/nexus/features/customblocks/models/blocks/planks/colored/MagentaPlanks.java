package gg.projecteden.nexus.features.customblocks.models.blocks.planks.colored;

import gg.projecteden.nexus.features.customblocks.models.interfaces.ICustomBlock;
import lombok.NonNull;
import org.bukkit.Instrument;

public class MagentaPlanks implements ICustomBlock {
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
		return null;
	}

	@Override
	public int getCustomModelData() {
		return 0;
	}
}
