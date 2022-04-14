package gg.projecteden.nexus.features.customblocks.models.blocks.terracotta_shingles;

import gg.projecteden.nexus.features.customblocks.models.interfaces.ICustomBlock;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import lombok.NonNull;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

public class GreenTerracottaShingles implements ICustomBlock {
	@Override
	public @NonNull Instrument getNoteBlockInstrument() {
		return Instrument.BIT;
	}

	@Override
	public int getNoteBlockStep() {
		return 5;
	}

	@Override
	public @NonNull String getName() {
		return "Green Terracotta Shingles";
	}

	@Override
	public int getCustomModelData() {
		return 20205;
	}

	@Override
	public @Nullable RecipeBuilder<?> getRecipe() {
		return get2x2Recipe(Material.GREEN_TERRACOTTA);
	}
}
