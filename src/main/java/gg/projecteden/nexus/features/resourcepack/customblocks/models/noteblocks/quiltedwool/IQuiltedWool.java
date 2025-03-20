package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.quiltedwool;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlockTag;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.IReDyeable;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.ICraftableNoteBlock;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IQuiltedWool extends IReDyeable, ICraftableNoteBlock {
	@Override
	default CustomBlockTag getReDyeTag() {
		return CustomBlockTag.QUILTED_WOOL;
	}

	default @NotNull Material getMaterial() {
		return Material.valueOf(StringUtils.camelToSnake(getClass().getSimpleName().replace("QuiltedWool", "")).toUpperCase() + "_WOOL");
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
		return 1.6;
	}

	@Override
	default double getBaseDiggingSpeedWithPreferredTool(ItemStack tool) {
		return 5;
	}

	@Override
	default Material getMinimumPreferredTool() {
		return Material.SHEARS;
	}

	@Override
	default boolean requiresCorrectToolForDrops() {
		return false;
	}

}
