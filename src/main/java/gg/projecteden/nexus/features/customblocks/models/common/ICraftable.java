package gg.projecteden.nexus.features.customblocks.models.common;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import lombok.NonNull;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

public interface ICraftable extends ICustomBlock {
	@NonNull Material getRecipeUnlockMaterial();

	default @Nullable Pair<RecipeBuilder<?>, Integer> getCraftRecipe() {
		return null;
	}

	default @Nullable RecipeBuilder<?> getUncraftRecipe() {
		return null;
	}
}
