package gg.projecteden.nexus.features.customblocks.models.blocks.planks;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.customblocks.models.interfaces.ICustomBlock;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import lombok.NonNull;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

public class CarvedDarkOakPlanks implements ICustomBlock {
	@Override
	public @NonNull Instrument getNoteBlockInstrument() {
		return Instrument.BANJO;
	}

	@Override
	public int getNoteBlockStep() {
		return 12;
	}

	@Override
	public @NonNull String getName() {
		return "Carved Dark Oak Planks";
	}

	@Override
	public int getCustomModelData() {
		return 20012;
	}

	@Override
	public @Nullable Pair<RecipeBuilder<?>, Integer> getCraftRecipe() {
		return getCombineSlabsRecipe(Material.DARK_OAK_SLAB);
	}
}
