package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.ice;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.ICraftableNoteBlock;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.ICustomNoteBlock;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

@CustomBlockConfig(
	name = "Chiseled Packed Snow",
	itemModel = ItemModelType.ICE_CHISELED_PACKED_SNOW
)
@CustomNoteBlockConfig(
	instrument = Instrument.BANJO,
	step = 3,
	powered = true,
	customBreakSound = "block.snow.break",
	customPlaceSound = "block.snow.place",
	customStepSound = "block.snow.step",
	customHitSound = "block.snow.hit",
	customFallSound = "block.snow.fall"
)
public class ChiseledPackedSnow implements ICustomNoteBlock, ICraftableNoteBlock {

	@Override
	public double getBlockHardness() {
		return 0.6;
	}

	@Override
	public Material getMinimumPreferredTool() {
		return Material.WOODEN_SHOVEL;
	}

	@Override
	public @Nullable Pair<RecipeBuilder<?>, Integer> getCraftRecipe() {
		return get1x2Recipe(CustomBlock.PACKED_SNOW.get().getItemStack(), 2);
	}
}
