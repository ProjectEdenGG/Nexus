package gg.projecteden.nexus.features.resourcepack.customblocks.models.common;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.recipes.models.NexusRecipe;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public interface ICraftable extends ICustomBlock {

	default @Nullable Pair<RecipeBuilder<?>, Integer> getCraftRecipe() {
		return null;
	}

	default @Nullable RecipeBuilder<?> getUncraftRecipe() {
		return null;
	}

	default List<NexusRecipe> getOtherRecipes() {
		return new ArrayList<>();
	}

	default @Nullable Pair<RecipeBuilder<?>, Integer> getCraftRecipe(@NotNull Material from, int makeAmount) {
		ItemStack fromItem = new ItemBuilder(from).build();
		return new Pair<>(RecipeBuilder.shapeless().add(fromItem).unlockedBy(getItemStack()).unlockedBy(from), makeAmount);
	}

	default @Nullable Pair<RecipeBuilder<?>, Integer> getCraftRecipe(@NotNull String from, int makeAmount) {
		ItemStack customBlockItem = CustomBlock.valueOf(from).get().getItemStack();
		return new Pair<>(RecipeBuilder.shapeless().add(customBlockItem).unlockedByItems(getItemStack(), customBlockItem), makeAmount);
	}

	default @Nullable RecipeBuilder<?> getUncraftRecipe(@NotNull Material toMake, int count) {
		ItemStack toMakeItem = new ItemBuilder(toMake).amount(count).build();
		return RecipeBuilder.shapeless().add(getItemStack()).toMake(toMakeItem).unlockedByItems(getItemStack(), toMakeItem);
	}

	default @Nullable RecipeBuilder<?> getUncraftRecipe(@NotNull String toMake, int count) {
		ItemStack toMakeItem = new ItemBuilder(CustomBlock.valueOf(toMake).get().getItemStack()).amount(count).build();
		return RecipeBuilder.shapeless().add(getItemStack()).toMake(toMakeItem).unlockedBy(getItemStack());
	}

	default Pair<RecipeBuilder<?>, Integer> get2x2Recipe(@NotNull Material material) {
		return get2x2Recipe(material, 4);
	}

	default Pair<RecipeBuilder<?>, Integer> get2x2Recipe(@NotNull Material material, int amount) {
		return get2x2Recipe(new ItemStack(material), amount);
	}

	default Pair<RecipeBuilder<?>, Integer> get2x2Recipe(@NotNull ItemStack itemStack, int amount) {
		return new Pair<>(RecipeBuilder.shaped("11", "11").add('1', itemStack).unlockedByItems(getItemStack(), itemStack), amount);
	}

	default Pair<RecipeBuilder<?>, Integer> getCombineSlab(@NotNull Material material) {
		return getCombineRecipeVertical(material, 1);
	}

	default Pair<RecipeBuilder<?>, Integer> getCombineRecipeVertical(@NotNull Material material, int resultAmount) {
		return new Pair<>(RecipeBuilder.shaped("1", "1").add('1', material).unlockedBy(getItemStack()).unlockedBy(material), resultAmount);
	}

	default Pair<RecipeBuilder<?>, Integer> getSurroundRecipe(@NonNull Material center, @NotNull Material surround) {
		return new Pair<>(RecipeBuilder.surround(center).with(surround).unlockedBy(getItemStack()).unlockedByMaterials(List.of(surround, center)), 8);
	}

	default Pair<RecipeBuilder<?>, Integer> getSurroundRecipe(@NonNull Material center, @NotNull Tag<Material> surround) {
		List<Material> unlockMaterials = new ArrayList<>(surround.getValues());
		unlockMaterials.add(center);

		return new Pair<>(RecipeBuilder.surround(center).with(surround).unlockedBy(getItemStack()).unlockedByMaterials(unlockMaterials), 8);
	}

	default Pair<RecipeBuilder<?>, Integer> getSurroundRecipe(@NonNull Material center, @NotNull List<ItemStack> surround) {
		return new Pair<>(RecipeBuilder.surround(center).with(surround).unlockedBy(getItemStack()), 8);
	}

}
