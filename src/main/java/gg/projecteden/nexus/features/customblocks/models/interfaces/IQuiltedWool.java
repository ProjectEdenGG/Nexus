package gg.projecteden.nexus.features.customblocks.models.interfaces;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.customblocks.models.CustomBlockTag;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IQuiltedWool extends IDyeable{
	@Override
	default @Nullable Pair<RecipeBuilder<?>, Integer> getCraftRecipe() {
		return get2x2Recipe(getWool());
	}

	@Override
	default @Nullable RecipeBuilder<?> getUncraftRecipe() {
		return getUncraftRecipe(getWool(), 1);
	}

	@Override
	default CustomBlockTag getRedyeTag(){
		return CustomBlockTag.QUILTED_WOOL;
	}

	@NotNull Material getWool();
}
