package gg.projecteden.nexus.features.customblocks.models.blocks.planks.vertical;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.customblocks.models.blocks.common.ICraftable;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import lombok.NonNull;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder.shaped;
import static gg.projecteden.nexus.utils.StringUtils.camelToSnake;

public interface IVerticalPlanks extends ICraftable {

	@Override
	default @Nullable Pair<RecipeBuilder<?>, Integer> getCraftRecipe() {
		return new Pair<>(shaped("1", "1", "1").add('1', getMaterial()), 1);
	}

	@NotNull
	private Material getMaterial() {
		final String woodType = getClass().getSimpleName().replace("Vertical", "").replace("Planks", "");
		return Material.valueOf(camelToSnake(woodType).toUpperCase() + "_PLANKS");
	}

	@Override
	default @NonNull Material getRecipeUnlockMaterial() {
		return getMaterial();
	}

}
