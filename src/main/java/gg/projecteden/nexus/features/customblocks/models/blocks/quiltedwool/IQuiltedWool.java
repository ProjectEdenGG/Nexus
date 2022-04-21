package gg.projecteden.nexus.features.customblocks.models.blocks.quiltedwool;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.customblocks.models.CustomBlockTag;
import gg.projecteden.nexus.features.customblocks.models.blocks.common.ICraftable;
import gg.projecteden.nexus.features.customblocks.models.blocks.common.IDyeable;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import lombok.NonNull;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static gg.projecteden.nexus.utils.StringUtils.camelToSnake;

public interface IQuiltedWool extends IDyeable, ICraftable {
	@Override
	default CustomBlockTag getRedyeTag() {
		return CustomBlockTag.QUILTED_WOOL;
	}

	default @NotNull Material getMaterial() {
		return Material.valueOf(camelToSnake(getClass().getSimpleName().replace("QuiltedWool", "")).toUpperCase() + "_WOOL");
	}

	@Override
	default @Nullable Pair<RecipeBuilder<?>, Integer> getCraftRecipe() {
		return get2x2Recipe(getMaterial(), 1);
	}

	@Override
	default Pair<RecipeBuilder<?>, Integer> getUncraftRecipe() {
		return get2x2Recipe(this.getItemStack(), 1);
	}

	@Override
	default @NonNull Material getRecipeUnlockMaterial() {
		return getMaterial();
	}
}
