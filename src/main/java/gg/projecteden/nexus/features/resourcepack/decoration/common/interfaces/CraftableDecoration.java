package gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces;

import gg.projecteden.nexus.features.recipes.models.NexusRecipe;
import gg.projecteden.nexus.features.recipes.models.RecipeGroup;
import gg.projecteden.nexus.features.recipes.models.RecipeType;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface CraftableDecoration {

	default boolean isCraftable() {
		return true;
	}

	default RecipeType getRecipeType() {
		return RecipeType.DECORATION;
	}

	default RecipeGroup getGroup() {
		return null;
	}

	default ItemStack getResult() {
		return null;
	}

	default RecipeBuilder<?> getRecipeBuilder() {
		return null;
	}

	default @Nullable NexusRecipe buildRecipe() {
		if (!isCraftable())
			return null;

		RecipeBuilder<?> builder = getRecipeBuilder();
		if (builder == null)
			return null;

		return builder.toMake(getResult()).build().group(getGroup()).type(getRecipeType());
	}
}
