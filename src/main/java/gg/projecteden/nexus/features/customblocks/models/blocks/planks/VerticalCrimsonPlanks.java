package gg.projecteden.nexus.features.customblocks.models.blocks.planks;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.customblocks.models.interfaces.ICustomBlock;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import lombok.NonNull;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

public class VerticalCrimsonPlanks implements ICustomBlock {
	@Override
	public @NonNull Instrument getNoteBlockInstrument() {
		return Instrument.BANJO;
	}

	@Override
	public int getNoteBlockStep() {
		return 13;
	}

	@Override
	public @NonNull String getName() {
		return "Vertical Crimson Planks";
	}

	@Override
	public int getCustomModelData() {
		return 20013;
	}

	@Override
	public @Nullable Pair<RecipeBuilder<?>, Integer> getCraftRecipe() {
		return getVerticalRecipe(Material.CRIMSON_PLANKS);
	}
}
