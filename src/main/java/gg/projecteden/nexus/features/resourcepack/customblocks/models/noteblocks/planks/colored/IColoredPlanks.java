package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.colored;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlockTag;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.IDyeable;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.IPlanks;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IColoredPlanks extends IDyeable, IPlanks {

	@Override
	default CustomBlockTag getRedyeTag() {
		return CustomBlockTag.COLORED_PLANKS;
	}

	default @NotNull Material getMaterial() {
		return Material.valueOf(StringUtils.camelToSnake(getClass().getSimpleName().replace("Planks", "")).toUpperCase() + "_DYE");
	}

	@Override
	default @Nullable Pair<RecipeBuilder<?>, Integer> getCraftRecipe() {
		return getSurroundRecipe(getMaterial(), MaterialTag.PLANKS);
	}

}
