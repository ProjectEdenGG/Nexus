package gg.projecteden.nexus.features.customblocks.models.interfaces;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.customblocks.models.CustomBlockTag;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

public interface IColoredPlanks extends IDyeable {

	@Override
	default CustomBlockTag getRedyeTag() {
		return CustomBlockTag.COLORED_PLANKS;
	}

	@Override
	default @Nullable Pair<RecipeBuilder<?>, Integer> getCraftRecipe() {
		return getSurroundRecipe(Material.valueOf(getClass().getSimpleName().replace("Planks", "").toUpperCase() + "_DYE"), MaterialTag.PLANKS);
	}

}
