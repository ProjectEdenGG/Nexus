package gg.projecteden.nexus.features.customblocks.models.blocks.planks;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.customblocks.models.interfaces.ICustomBlock;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import lombok.NonNull;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

public class VerticalBirchPlanks implements ICustomBlock {
	@Override
	public @NonNull Instrument getNoteBlockInstrument() {
		return Instrument.BANJO;
	}

	@Override
	public int getNoteBlockStep() {
		return 5;
	}

	@Override
	public @NonNull String getName() {
		return "Vertical Birch Planks";
	}

	@Override
	public int getCustomModelData() {
		return 20005;
	}

	@Override
	public @Nullable Pair<RecipeBuilder<?>, Integer> getCraftRecipe() {
		return getVerticalRecipe(Material.BIRCH_PLANKS);
	}
}
