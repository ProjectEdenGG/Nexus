package gg.projecteden.nexus.features.customblocks.models.blocks;

import gg.projecteden.nexus.features.customblocks.models.ICustomBlock;
import lombok.NonNull;
import org.bukkit.Instrument;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.Nullable;

public class PotatoCrate implements ICustomBlock {
	@Override
	public @NonNull Instrument getNoteBlockInstrument() {
		return Instrument.BASS_DRUM;
	}

	@Override
	public int getNoteBlockStep() {
		return 5;
	}

	@Override
	public String getName() {
		return "Crate of Potatoes";
	}

	@Override
	public int getCustomModelData() {
		return 20055;
	}

	@Override
	public @Nullable Recipe getRecipe() {
		return null; //TODO
	}
}
