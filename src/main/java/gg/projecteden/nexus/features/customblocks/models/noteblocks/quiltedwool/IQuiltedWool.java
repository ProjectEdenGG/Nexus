package gg.projecteden.nexus.features.customblocks.models.noteblocks.quiltedwool;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.customblocks.models.CustomBlockTag;
import gg.projecteden.nexus.features.customblocks.models.common.IDyeable;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.ICraftableNoteBlock;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static gg.projecteden.nexus.utils.StringUtils.camelToSnake;

public interface IQuiltedWool extends IDyeable, ICraftableNoteBlock {
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
	default @Nullable RecipeBuilder<?> getUncraftRecipe() {
		return getUncraftRecipe(getMaterial(), 4);
	}

	@Override
	default double getBlockHardness() {
		return 1.2;
	}

	@Override
	default boolean requiresCorrectToolForDrops() {
		return false;
	}

	@Override
	default double getSpeedMultiplier(ItemStack tool) {
		if (tool.getType() == Material.SHEARS)
			return 5;

		return IDyeable.super.getSpeedMultiplier(tool);
	}
}