package gg.projecteden.nexus.features.workbenches;

import gg.projecteden.nexus.features.recipes.models.RecipeType;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;

import java.util.List;

public interface ICraftableCustomBench {

	default RecipeBuilder<?> getBenchRecipe() {
		return null;
	}

	default List<RecipeBuilder<?>> getAdditionRecipes() {
		return null;
	}

	default RecipeType getRecipeType() {
		return RecipeType.FUNCTIONAL;
	}

}
