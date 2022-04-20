package gg.projecteden.nexus.features.customblocks.models.interfaces;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ICompacted extends ICustomBlock {
	@Override
	default @Nullable Pair<RecipeBuilder<?>, Integer> getCraftRecipe() {
		return getCompactRecipe(getMaterial());
	}

	@Override
	default RecipeBuilder<?> getUncraftRecipe() {
		return getUncraftRecipe(getMaterial(), 9);
	}

	@NotNull Material getMaterial();
}
