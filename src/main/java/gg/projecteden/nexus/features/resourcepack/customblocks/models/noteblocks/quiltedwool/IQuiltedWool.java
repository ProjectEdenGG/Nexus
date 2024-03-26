package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.quiltedwool;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlockTag;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.IDyeable;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.ICraftableNoteBlock;
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
