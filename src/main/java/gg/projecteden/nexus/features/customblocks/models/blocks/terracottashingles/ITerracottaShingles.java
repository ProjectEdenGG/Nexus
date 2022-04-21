package gg.projecteden.nexus.features.customblocks.models.blocks.terracottashingles;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.customblocks.models.CustomBlockTag;
import gg.projecteden.nexus.features.customblocks.models.blocks.common.IDyeable;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import static gg.projecteden.nexus.utils.StringUtils.camelToSnake;

public interface ITerracottaShingles extends IDyeable {

	@Override
	default CustomBlockTag getRedyeTag() {
		return CustomBlockTag.TERRACOTTA_SHINGLES;
	}

	default Material getMaterial() {
		return Material.valueOf(camelToSnake(getClass().getSimpleName().replace("TerracottaShingles", "")).toUpperCase() + "_TERRACOTTA");
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
