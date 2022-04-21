package gg.projecteden.nexus.features.customblocks.models.blocks.common;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder.shaped;
import static gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder.shapeless;
import static gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder.surround;

public interface ICraftable extends ICustomBlock {

	@NonNull Material getRecipeUnlockMaterial();

	default @Nullable Pair<RecipeBuilder<?>, Integer> getCraftRecipe() {
		return null;
	}

	default @Nullable Pair<RecipeBuilder<?>, Integer> getUncraftRecipe() {
		return null;
	}

	default @Nullable Pair<RecipeBuilder<?>, Integer> getUncraftRecipe(@NotNull Material toMake, int count) {
		return new Pair<>(shapeless().add(getItemStack()).toMake(new ItemStack(toMake)), count);
	}

	default Pair<RecipeBuilder<?>, Integer> get2x2Recipe(@NotNull Material material) {
		return get2x2Recipe(material, 4);
	}

	default Pair<RecipeBuilder<?>, Integer> get2x2Recipe(@NotNull Material material, int amount) {
		return get2x2Recipe(new ItemStack(material), amount);
	}

	default Pair<RecipeBuilder<?>, Integer> get2x2Recipe(@NotNull ItemStack itemStack, int amount) {
		return new Pair<>(shaped("11", "11").add('1', itemStack), amount);
	}

	default Pair<RecipeBuilder<?>, Integer> getCombineSlabsRecipe(@NotNull Material material) {
		return new Pair<>(shaped("1", "1").add('1', material), 1);
	}

	default Pair<RecipeBuilder<?>, Integer> getSurroundRecipe(@NonNull Material center, @NotNull Tag<Material> surround) {
		return new Pair<>(surround(center).with(surround), 8);
	}
}
