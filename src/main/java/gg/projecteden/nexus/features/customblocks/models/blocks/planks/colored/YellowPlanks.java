package gg.projecteden.nexus.features.customblocks.models.blocks.planks.colored;

import gg.projecteden.nexus.features.customblocks.models.interfaces.ICustomBlock;
import lombok.NonNull;
import org.bukkit.Instrument;

public class YellowPlanks implements ICustomBlock {
	@Override
	public @NonNull Instrument getNoteBlockInstrument() {
		return Instrument.BELL;
	}

	@Override
	public int getNoteBlockStep() {
		return 3;
	}

	@Override
	public @NonNull String getName() {
		return "Yellow Planks";
	}

	@Override
	public int getCustomModelData() {
		return 20153;
	}
}
