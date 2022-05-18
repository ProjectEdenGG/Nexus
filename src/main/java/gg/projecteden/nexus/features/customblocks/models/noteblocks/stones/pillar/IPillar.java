package gg.projecteden.nexus.features.customblocks.models.noteblocks.stones.pillar;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.ICraftableNoteBlock;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import lombok.NonNull;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface IPillar extends ICraftableNoteBlock {

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
	default @NonNull Material getRecipeUnlockMaterial() {
		return getMaterial();
	}

	@Override
	default Set<Material> getApplicableTools() {
		return MaterialTag.PICKAXES.getValues();
	}
}
