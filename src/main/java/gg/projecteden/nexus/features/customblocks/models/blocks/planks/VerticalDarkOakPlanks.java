package gg.projecteden.nexus.features.customblocks.models.blocks.planks;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.customblocks.models.interfaces.ICustomBlock;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import lombok.NonNull;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

public class VerticalDarkOakPlanks implements ICustomBlock {
	@Override
	public @NonNull Instrument getNoteBlockInstrument() {
		return Instrument.BANJO;
	}

	@Override
	public int getNoteBlockStep() {
		return 11;
	}

	@Override
	public @NonNull String getName() {
		return "Vertical Dark Oak Planks";
	}

	@Override
	public int getCustomModelData() {
		return 20011;
	}

	@Override
	public @Nullable Pair<RecipeBuilder<?>, Integer> getRecipe() {
		return getVerticalRecipe(Material.DARK_OAK_PLANKS);
	}
}
