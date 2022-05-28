package gg.projecteden.nexus.features.customblocks.models.noteblocks.stones.chiseled;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.ICraftableNoteBlock;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface IChiseledStone extends ICraftableNoteBlock {

	@NotNull
	default Material getMaterial() {
		return Material.valueOf(("POLISHED_" + getClass().getSimpleName().replace("Chiseled", "") + "_SLAB").toUpperCase());
	}

	@Override
	default @Nullable Pair<RecipeBuilder<?>, Integer> getCraftRecipe() {
		return getCombineSlab(getMaterial());
	}

	@Override
	default @Nullable RecipeBuilder<?> getUncraftRecipe() {
		return getUncraftRecipe(getMaterial(), 2);
	}

	@Override
	default @Nullable Material getRecipeUnlockMaterial() {
		return getMaterial();
	}

	@Override
	default Set<Material> getApplicableTools() {
		return MaterialTag.PICKAXES.getValues();
	}

}
