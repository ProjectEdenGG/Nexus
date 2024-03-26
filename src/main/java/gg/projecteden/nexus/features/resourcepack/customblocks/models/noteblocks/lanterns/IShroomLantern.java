package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.lanterns;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.ICraftableNoteBlock;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import static gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder.shaped;

public interface IShroomLantern extends ILantern, ICraftableNoteBlock {

	@Override
	default @Nullable Pair<RecipeBuilder<?>, Integer> getCraftRecipe() {
		return new Pair<>(shaped("121", "333", "121")
			.add('1', Material.STICK)
			.add('2', getMaterial())
			.add('3', Material.SHROOMLIGHT)
			.unlockedBy(getItemStack())
			.unlockedBy(Material.SHROOMLIGHT), 1);
	}

}
