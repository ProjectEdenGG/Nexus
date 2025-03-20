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
	name = "Chiseled Packed Ice Bricks",
	itemModel = ItemModelType.ICE_CHISELED_PACKED_ICE_BRICKS
)
@CustomNoteBlockConfig(
	instrument = Instrument.BANJO,
	step = 6,
	powered = true,
	customBreakSound = "block.glass.break",
	customPlaceSound = "block.glass.place",
	customStepSound = "block.glass.step",
	customHitSound = "block.glass.hit",
	customFallSound = "block.glass.fall"
)
public class ChiseledPackedIceBricks implements ICustomNoteBlock, ICraftableNoteBlock {

	@Override
	public double getBlockHardness() {
		return 0.8;
	}

	@Override
	public Material getMinimumPreferredTool() {
		return Material.WOODEN_PICKAXE;
	}

	@Override
	public boolean requiresSilkTouchForDrops() {
		return true;
	}

	@Override
	public boolean requiresCorrectToolForDrops() {
		return true;
	}

	@Override
	public @Nullable Pair<RecipeBuilder<?>, Integer> getCraftRecipe() {
		return get1x2Recipe(CustomBlock.POLISHED_PACKED_ICE.get().getItemStack(), 2);
	}
}
