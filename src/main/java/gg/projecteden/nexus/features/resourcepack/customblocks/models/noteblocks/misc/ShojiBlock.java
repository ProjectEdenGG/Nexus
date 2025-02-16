package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.misc;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.DirectionalConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.ICraftableNoteBlock;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.IDirectionalNoteBlock;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

@CustomBlockConfig(
	name = "Shoji Block",
	itemModel = ItemModelType.MISC_SHOJI_BLOCK
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
	public @Nullable Pair<RecipeBuilder<?>, Integer> getCraftRecipe() {
		return new Pair<>(RecipeBuilder.shaped("121", "212", "121")
			.add('1', Material.STICK)
			.add('2', Material.PAPER)
			.unlockedBy(getItemStack())
			.unlockedBy(Material.PAPER), 4);
	}

	@Override
	public double getBlockHardness() {
		return 0.8;
	}

	@Override
	public boolean requiresCorrectToolForDrops() {
		return false;
	}

}
