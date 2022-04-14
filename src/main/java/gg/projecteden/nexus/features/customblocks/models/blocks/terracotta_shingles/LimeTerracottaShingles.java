package gg.projecteden.nexus.features.customblocks.models.blocks.terracotta_shingles;

import gg.projecteden.nexus.features.customblocks.models.interfaces.ICustomBlock;
import lombok.NonNull;
import org.bukkit.Instrument;

public class LimeTerracottaShingles implements ICustomBlock {
	@Override
	public @NonNull Instrument getNoteBlockInstrument() {
		return Instrument.BIT;
	}

	@Override
	public int getNoteBlockStep() {
		return 4;
	}

	@Override
	public @NonNull String getName() {
		return "Lime Terracotta Shingles";
	}

	@Override
	public int getCustomModelData() {
		return 20204;
	}
}
