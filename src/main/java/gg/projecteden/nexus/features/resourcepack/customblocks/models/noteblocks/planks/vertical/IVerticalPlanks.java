package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.vertical;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.IPlanks;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IVerticalPlanks extends IPlanks {

	@Override
	default @Nullable Pair<RecipeBuilder<?>, Integer> getCraftRecipe() {
		return new Pair<>(RecipeBuilder.shaped("1", "1", "1").add('1', getMaterial()), 3);
	}

	@Override
	default @Nullable RecipeBuilder<?> getUncraftRecipe() {
		return getUncraftRecipe(getMaterial(), 1);
	}

	@NotNull
	private Material getMaterial() {
		final String woodType = getClass().getSimpleName().replace("Vertical", "").replace("Planks", "");
		String material = StringUtils.camelToSnake(woodType).toUpperCase() + "_PLANKS";
		return Material.valueOf(material);
	}

}
