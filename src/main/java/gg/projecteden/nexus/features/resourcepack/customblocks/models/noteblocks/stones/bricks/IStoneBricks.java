package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.stones.bricks;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.recipes.models.NexusRecipe;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.ICraftableNoteBlock;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IStoneBricks extends ICraftableNoteBlock {

	@NotNull
	private Material getMaterial() {
		return Material.valueOf("POLISHED_" + getClass().getSimpleName().replace("Bricks", "").toUpperCase());
	}

	@Override
	default @Nullable Pair<RecipeBuilder<?>, Integer> getCraftRecipe() {
		return get2x2Recipe(getMaterial(), 4);
	}

	@Override
	default @Nullable RecipeBuilder<?> getUncraftRecipe() {
		return getUncraftRecipe(getMaterial(), 4);
	}

	@Override
	default List<NexusRecipe> getOtherRecipes() {
		return List.of(
			RecipeBuilder.stoneCutter(getMaterial()).toMake(getItemStack())
				.unlockedBy(getItemStack())
				.unlockedBy(getMaterial())
				.build()
		);
	}

	@Override
	default double getBlockHardness() {
		return 1.5;
	}

	@Override
	default Material getMinimumPreferredTool() {
		return Material.WOODEN_PICKAXE;
	}

}
