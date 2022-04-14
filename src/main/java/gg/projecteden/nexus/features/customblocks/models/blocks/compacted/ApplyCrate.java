package gg.projecteden.nexus.features.customblocks.models.blocks.compacted;

import gg.projecteden.nexus.features.customblocks.models.interfaces.ICustomBlock;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import lombok.NonNull;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ApplyCrate implements ICustomBlock {
	@Override
	public @NonNull Instrument getNoteBlockInstrument() {
		return Instrument.BASS_DRUM;
	}

	@Override
	public int getNoteBlockStep() {
		return 1;
	}

	@Override
	public @NotNull String getName() {
		return "Crate of Apples";
	}

	@Override
	public int getCustomModelData() {
		return 20051;
	}

	@Override
	public @Nullable RecipeBuilder<?> getRecipe() {
		return compacted(Material.APPLE);
	}
}
