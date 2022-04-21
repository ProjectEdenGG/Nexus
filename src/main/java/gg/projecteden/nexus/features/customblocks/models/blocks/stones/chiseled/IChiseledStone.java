package gg.projecteden.nexus.features.customblocks.models.blocks.stones.chiseled;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.customblocks.models.blocks.common.ICraftable;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import lombok.NonNull;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder.shaped;

public interface IChiseledStone extends ICraftable {

	@NotNull
	default Material getMaterial() {
		return Material.valueOf(("POLISHED_" + getClass().getSimpleName().replace("Chiseled", "") + "_SLAB").toUpperCase());
	}

	@Override
	default @Nullable Pair<RecipeBuilder<?>, Integer> getCraftRecipe() {
		return new Pair<>(shaped("11").add('1', getMaterial()), 1);
	}

	@Override
	default @Nullable RecipeBuilder<?> getUncraftRecipe() {
		return getUncraftRecipe(getMaterial(), 2);
	}

	@Override
	default @NonNull Material getRecipeUnlockMaterial() {
		return getMaterial();
	}

}
