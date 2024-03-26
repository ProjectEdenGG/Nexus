package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.compacted;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.ICraftableNoteBlock;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder.shaped;

public interface ICompacted extends ICraftableNoteBlock {

	@Override
	default @Nullable Pair<RecipeBuilder<?>, Integer> getCraftRecipe() {
		return new Pair<>(shaped("111", "111", "111").add('1', getMaterial())
			.unlockedBy(getItemStack())
			.unlockedBy(getMaterial()), 1);
	}

	@Override
	default @Nullable RecipeBuilder<?> getUncraftRecipe() {
		return getUncraftRecipe(getMaterial(), 9);
	}

	@NotNull Material getMaterial();

}
