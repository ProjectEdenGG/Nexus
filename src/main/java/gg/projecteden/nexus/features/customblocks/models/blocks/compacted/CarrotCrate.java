package gg.projecteden.nexus.features.customblocks.models.blocks.compacted;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.customblocks.models.interfaces.ICustomBlock;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import lombok.NonNull;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
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
	public @NotNull String getName() {
		return "Crate of Carrots";
	}

	@Override
	public int getCustomModelData() {
		return 20054;
	}

	@Override
	public @Nullable Pair<RecipeBuilder<?>, Integer> getCraftRecipe() {
		return getCompactRecipe(Material.CARROT);
	}

	@Override
	public RecipeBuilder<?> getUncraftRecipe() {
		return getUncraftRecipe(Material.CARROT, 9);
	}
}
