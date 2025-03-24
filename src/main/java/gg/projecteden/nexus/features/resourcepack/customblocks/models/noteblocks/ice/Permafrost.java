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
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@CustomBlockConfig(
	name = "Permafrost",
	itemModel = ItemModelType.ICE_PERMAFROST
)
@CustomNoteBlockConfig(
	instrument = Instrument.BANJO,
	step = 7,
	powered = true,
	customBreakSound = "block.grass.break",
	customPlaceSound = "block.grass.place",
	customStepSound = "block.grass.step",
	customHitSound = "block.grass.hit",
	customFallSound = "block.grass.fall"
)
public class Permafrost implements ICustomNoteBlock, ICraftableNoteBlock {

	@Override
	public double getBlockHardness() {
		return 0.8;
	}

	@Override
	public Material getMinimumPreferredTool() {
		return Material.WOODEN_SHOVEL;
	}

	@Override
	public @Nullable Pair<RecipeBuilder<?>, Integer> getCraftRecipe() {
		return new Pair<>(RecipeBuilder.shaped("1", "2").add('1', Material.SNOW).add('2', Material.DIRT)
			.unlockedByItems(getItemStack(), new ItemStack(Material.DIRT)), 1);
	}
}
