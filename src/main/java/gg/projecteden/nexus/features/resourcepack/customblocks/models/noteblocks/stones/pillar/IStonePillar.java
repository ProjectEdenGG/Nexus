package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.stones.pillar;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.recipes.models.NexusRecipe;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.ICraftableNoteBlock;
import lombok.NonNull;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IStonePillar extends ICraftableNoteBlock {

	@NonNull Material getMaterial();

	@Override
	default @Nullable Pair<RecipeBuilder<?>, Integer> getCraftRecipe() {
		return getCombineRecipeVertical(getMaterial(), 2);
	}

	@Override
	default @Nullable RecipeBuilder<?> getUncraftRecipe() {
		return getUncraftRecipe(getMaterial(), 2);
	}

	@Override
	default List<NexusRecipe> getOtherRecipes() {
		return List.of(
			RecipeBuilder.stoneCutter(getMaterial()).toMake(getItemStack())
				.unlockedBy(getItemStack())
				.unlockedBy(getMaterial())
				.build()
		);
	}

	@Override
	default double getBlockHardness() {
		return 2.0;
	}

	@Override
	default Material getMinimumPreferredTool() {
		return Material.WOODEN_PICKAXE;
	}

}
