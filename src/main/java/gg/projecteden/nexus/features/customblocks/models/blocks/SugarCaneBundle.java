package gg.projecteden.nexus.features.customblocks.models.blocks;

import gg.projecteden.nexus.features.customblocks.models.ICustomBlock;
import lombok.NonNull;
import org.bukkit.Instrument;

public class SugarCaneBundle implements ICustomBlock {
	@Override
	public @NonNull Instrument getNoteBlockInstrument() {
		return Instrument.BASS_DRUM;
	}

	@Override
	public int getNoteBlockStep() {
		return 10;
	}

	@Override
	public String getName() {
		return "Bundle of Sugar Cane";
	}

	@Override
	public int getCustomModelData() {
		return 20069;
	}

	@Override
	public boolean canPlaceSideways() {
		return true;
	}

	public @NonNull Instrument getNoteBlockInstrument_NS() {
		return getNoteBlockInstrument();
	}

	public @NonNull Instrument getNoteBlockInstrument_EW() {
		return getNoteBlockInstrument();
	}

	public int getNoteBlockStep_NS() {
		return 11;
	}

	public int getNoteBlockStep_EW() {
		return 12;
	}
}
