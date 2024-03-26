package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.terracottashingles;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.ICraftableNoteBlock;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

public interface ITerracottaShingles extends ICraftableNoteBlock {

	Material getMaterial();

	@Override
	default @Nullable Pair<RecipeBuilder<?>, Integer> getCraftRecipe() {
		return get2x2Recipe(getMaterial());
	}

	@Override
	default @Nullable RecipeBuilder<?> getUncraftRecipe() {
		return getUncraftRecipe(getMaterial(), 4);
	}

	@Override
	default double getBlockHardness() {
		return 1.25;
	}

	@Override
	default Material getMinimumPreferredTool() {
		return Material.WOODEN_PICKAXE;
	}

}
