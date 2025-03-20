package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.ice;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.ICraftableNoteBlock;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.ICustomNoteBlock;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

@CustomBlockConfig(
	name = "Polished Packed Ice",
	itemModel = ItemModelType.ICE_POLISHED_PACKED_ICE
)
@CustomNoteBlockConfig(
	instrument = Instrument.BANJO,
	step = 5,
	powered = true,
	customBreakSound = "block.glass.break",
	customPlaceSound = "block.glass.place",
	customStepSound = "block.glass.step",
	customHitSound = "block.glass.hit",
	customFallSound = "block.glass.fall"
)
public class PolishedPackedIce implements ICustomNoteBlock, ICraftableNoteBlock {

	@Override
	public double getBlockHardness() {
		return 1.0;
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
		return get2x2Recipe(Material.PACKED_ICE, 4);
	}
}
