package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.carved;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.IPlanks;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static gg.projecteden.nexus.utils.StringUtils.camelToSnake;

public interface ICarvedPlanks extends IPlanks {

	// TODO: change recipe, current one replaces STICKS
	@Override
	default @Nullable Pair<RecipeBuilder<?>, Integer> getCraftRecipe() {
		return getCombineSlab(getMaterial());
	}

	@Override
	default @Nullable RecipeBuilder<?> getUncraftRecipe() {
		return getUncraftRecipe(getMaterial(), 2);
	}

	@NotNull
	private Material getMaterial() {
		final String woodType = getClass().getSimpleName().replace("Carved", "").replace("Planks", "");
		return Material.valueOf(camelToSnake(woodType).toUpperCase() + "_SLAB");
	}

}
