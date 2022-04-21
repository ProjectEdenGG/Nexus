package gg.projecteden.nexus.features.customblocks.models.blocks.common;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder.shaped;
import static gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder.surround;

public interface ICraftable extends ICustomBlock {

	@NonNull Material getRecipeUnlockMaterial();

	default @Nullable Pair<RecipeBuilder<?>, Integer> getCraftRecipe() {
		return null;
	}

	default @Nullable RecipeBuilder<?> getUncraftRecipe() {
		return null;
	}

	default RecipeBuilder<?> getUncraftRecipe(@NotNull Material toMake, int count) {
		ItemStack toMakeItem = new ItemBuilder(toMake).amount(count).build();
		return RecipeBuilder.shapeless().add(getItemStack()).toMake(toMakeItem);
	}

	default Pair<RecipeBuilder<?>, Integer> get2x2Recipe(@NotNull Material material) {
		return new Pair<>(shaped("11", "11").add('1', material), 4);
	}

	default Pair<RecipeBuilder<?>, Integer> getCombineSlabsRecipe(@NotNull Material material) {
		return new Pair<>(shaped("1", "1").add('1', material), 1);
	}

	default Pair<RecipeBuilder<?>, Integer> getSurroundRecipe(@NonNull Material center, @NotNull Tag<Material> surround) {
		return new Pair<>(surround(center).with(surround), 8);
	}
}
