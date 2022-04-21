package gg.projecteden.nexus.features.customblocks.models.blocks.stones.bricks;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.customblocks.models.blocks.common.ICraftable;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import lombok.NonNull;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IStoneBricks extends ICraftable {

	@NotNull
	private Material getMaterial() {
		return Material.valueOf("POLISHED_" + getClass().getSimpleName().replace("Bricks", "").toUpperCase());
	}

	@Override
	default @Nullable Pair<RecipeBuilder<?>, Integer> getCraftRecipe() {
		return get2x2Recipe(getMaterial());
	}

	@Override
	default Pair<RecipeBuilder<?>, Integer> getUncraftRecipe() {
		return getUncraftRecipe(getMaterial(), 1);
	}

	@Override
	default @NonNull Material getRecipeUnlockMaterial() {
		return getMaterial();
	}
}
