package gg.projecteden.nexus.features.customblocks.models.noteblocks.stones.pillar;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.ICraftableNoteBlock;
import gg.projecteden.nexus.features.recipes.models.NexusRecipe;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import lombok.NonNull;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

import static gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder.stoneCutter;

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
			stoneCutter(getMaterial()).toMake(getItemStack())
				.unlockedBy(getItemStack())
				.unlockedBy(getMaterial())
				.build()
		);
	}

	@Override
	default Set<Material> getApplicableTools() {
		return MaterialTag.PICKAXES.getValues();
	}
}
