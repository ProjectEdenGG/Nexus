package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.lanterns;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.ICraftableNoteBlock;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

public interface IPaperLantern extends ILantern, ICraftableNoteBlock {
	@Override
	default @Nullable Pair<RecipeBuilder<?>, Integer> getCraftRecipe() {
		return new Pair<>(RecipeBuilder.shaped("121", "343", "121")
			.add('1', Material.STICK)
			.add('2', getMaterial())
			.add('3', Material.PAPER)
			.add('4', Material.TORCH)
			.unlockedBy(getItemStack())
			.unlockedBy(Material.TORCH), 1);
	}

}
