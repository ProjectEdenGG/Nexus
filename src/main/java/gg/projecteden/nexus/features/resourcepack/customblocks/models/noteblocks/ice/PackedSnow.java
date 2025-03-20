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
	name = "Packed Snow",
	itemModel = ItemModelType.ICE_PACKED_SNOW
)
@CustomNoteBlockConfig(
	instrument = Instrument.BANJO,
	step = 2,
	powered = true,
	customBreakSound = "block.snow.break",
	customPlaceSound = "block.snow.place",
	customStepSound = "block.snow.step",
	customHitSound = "block.snow.hit",
	customFallSound = "block.snow.fall"
)
public class PackedSnow implements ICustomNoteBlock, ICraftableNoteBlock {

	@Override
	public double getBlockHardness() {
		return 0.4;
	}

	@Override
	public Material getMinimumPreferredTool() {
		return Material.WOODEN_SHOVEL;
	}

	@Override
	public @Nullable Pair<RecipeBuilder<?>, Integer> getCraftRecipe() {
		return get2x2Recipe(Material.SNOW_BLOCK, 4);
	}
}
