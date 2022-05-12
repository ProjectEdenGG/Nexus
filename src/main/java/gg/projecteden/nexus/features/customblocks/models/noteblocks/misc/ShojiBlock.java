package gg.projecteden.nexus.features.customblocks.models.noteblocks.misc;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.DirectionalConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.ICraftableNoteBlock;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.IDirectionalNoteBlock;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import lombok.NonNull;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import static gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder.shaped;

@CustomBlockConfig(
	name = "Shoji Block",
	modelId = 20122
)
@CustomNoteBlockConfig(
	instrument = Instrument.BASS_GUITAR,
	step = 22
)
@DirectionalConfig(
	step_NS = 23,
	step_EW = 24
)
public class ShojiBlock implements ICraftableNoteBlock, IDirectionalNoteBlock {

	@Override
	public @NonNull Material getRecipeUnlockMaterial() {
		return Material.PAPER;
	}

	@Override
	public @Nullable Pair<RecipeBuilder<?>, Integer> getCraftRecipe() {
		return new Pair<>(shaped("121", "212", "121")
			.add('1', Material.STICK)
			.add('2', Material.PAPER), 4);
	}
}
