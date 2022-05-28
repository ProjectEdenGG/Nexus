package gg.projecteden.nexus.features.customblocks.models.noteblocks.stones.chiseled;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.ICraftableNoteBlock;
import gg.projecteden.nexus.features.recipes.models.NexusRecipe;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

import static gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder.stoneCutter;

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
	default List<NexusRecipe> getOtherRecipes() {
		return List.of(
			stoneCutter(getMaterial()).toMake(getItemStack()).build()
		);
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
