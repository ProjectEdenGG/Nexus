package gg.projecteden.nexus.features.customblocks.models.blocks;

import gg.projecteden.nexus.features.customblocks.models.ICustomBlock;
import lombok.NonNull;
import org.bukkit.Instrument;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.Nullable;

public class CarrotCrate implements ICustomBlock {
	@Override
	public @NonNull Instrument getNoteBlockInstrument() {
		return Instrument.BASS_DRUM;
	}

	@Override
	public int getNoteBlockStep() {
		return 4;
	}

	@Override
	public String getName() {
		return "Crate of Carrots";
	}

	@Override
	public int getCustomModelData() {
		return 20054;
	}

	@Override
	public @Nullable Recipe getRecipe() {
		return null; //TODO
	}
}
