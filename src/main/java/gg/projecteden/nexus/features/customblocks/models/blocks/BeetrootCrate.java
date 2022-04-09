package gg.projecteden.nexus.features.customblocks.models.blocks;

import gg.projecteden.nexus.features.customblocks.models.ICustomBlock;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import lombok.NonNull;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BeetrootCrate implements ICustomBlock {
	@Override
	public @NonNull Instrument getNoteBlockInstrument() {
		return Instrument.BASS_DRUM;
	}

	@Override
	public int getNoteBlockStep() {
		return 2;
	}

	@Override
	public @NotNull String getName() {
		return "Crate of Beetroot";
	}

	@Override
	public int getCustomModelData() {
		return 20052;
	}

	@Override
	public @Nullable RecipeBuilder<?> getRecipe() {
		return compacted(Material.BEETROOT);
	}
}
