package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.concretebricks;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlockTag;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.IDyeable;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.ICraftableNoteBlock;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import static gg.projecteden.nexus.utils.StringUtils.camelToSnake;

public interface IConcreteBricks extends IDyeable, ICraftableNoteBlock {
	@Override
	default CustomBlockTag getRedyeTag() {
		return CustomBlockTag.CONCRETE_BRICKS;
	}

	default Material getMaterial() {
		return Material.valueOf(camelToSnake(getClass().getSimpleName().replace("ConcreteBricks", "")).toUpperCase() + "_CONCRETE");
	}

	@Override
	default @Nullable Pair<RecipeBuilder<?>, Integer> getCraftRecipe() {
		return get2x2Recipe(getMaterial());
	}

	@Override
	default @Nullable RecipeBuilder<?> getUncraftRecipe() {
		return getUncraftRecipe(getMaterial(), 4);
	}

	@Override
	default double getBlockHardness() {
		return 1.8;
	}

	@Override
	default Material getMinimumPreferredTool() {
		return Material.WOODEN_PICKAXE;
	}

}
