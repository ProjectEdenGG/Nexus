package gg.projecteden.nexus.features.customblocks.models.blocks.concrete_bricks;

import gg.projecteden.nexus.features.customblocks.models.interfaces.ICustomBlock;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import lombok.NonNull;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

public class BrownConcreteBricks implements ICustomBlock {
	@Override
	public @NonNull Instrument getNoteBlockInstrument() {
		return Instrument.CHIME;
	}

	@Override
	public int getNoteBlockStep() {
		return 12;
	}

	@Override
	public @NonNull String getName() {
		return "Brown Concrete Bricks";
	}

	@Override
	public int getCustomModelData() {
		return 20262;
	}

	@Override
	public @Nullable RecipeBuilder<?> getRecipe() {
		return get2x2Recipe(Material.BROWN_CONCRETE);
	}
}
