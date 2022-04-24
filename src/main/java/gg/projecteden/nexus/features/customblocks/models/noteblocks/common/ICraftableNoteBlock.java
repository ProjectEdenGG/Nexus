package gg.projecteden.nexus.features.customblocks.models.noteblocks.common;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.customblocks.models.common.ICraftable;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder.shaped;
import static gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder.shapeless;
import static gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder.surround;

public interface ICraftableNoteBlock extends ICraftable, ICustomNoteBlock {

	default @Nullable RecipeBuilder<?> getUncraftRecipe(@NotNull Material toMake, int count) {
		ItemStack toMakeItem = new ItemBuilder(toMake).amount(count).build();
		return shapeless().add(getItemStack()).toMake(toMakeItem);
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
