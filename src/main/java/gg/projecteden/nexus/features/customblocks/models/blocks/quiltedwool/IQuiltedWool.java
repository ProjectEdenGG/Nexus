package gg.projecteden.nexus.features.customblocks.models.blocks.quiltedwool;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.customblocks.models.CustomBlockTag;
import gg.projecteden.nexus.features.customblocks.models.blocks.common.IDyeable;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static gg.projecteden.nexus.utils.StringUtils.camelToSnake;

public interface IQuiltedWool extends IDyeable {
	@Override
	default CustomBlockTag getRedyeTag() {
		return CustomBlockTag.QUILTED_WOOL;
	}

	default @NotNull Material getMaterial() {
		return Material.valueOf(camelToSnake(getClass().getSimpleName().replace("QuiltedWool", "")).toUpperCase() + "_WOOL");
	}

	@Override
	default @Nullable Pair<RecipeBuilder<?>, Integer> getCraftRecipe() {
		return get2x2Recipe(getMaterial());
	}

	@Override
	default @Nullable RecipeBuilder<?> getUncraftRecipe() {
		return getUncraftRecipe(getMaterial(), 1);
	}
}
