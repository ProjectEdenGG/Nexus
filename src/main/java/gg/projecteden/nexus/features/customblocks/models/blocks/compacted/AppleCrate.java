package gg.projecteden.nexus.features.customblocks.models.blocks.compacted;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.customblocks.models.interfaces.ICustomBlock;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import lombok.NonNull;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AppleCrate implements ICustomBlock {
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
	public @Nullable Pair<RecipeBuilder<?>, Integer> getCraftRecipe() {
		return getCompactRecipe(Material.APPLE);
	}

	@Override
	public RecipeBuilder<?> getUncraftRecipe() {
		return getUncraftRecipe(Material.APPLE, 9);
	}
}
