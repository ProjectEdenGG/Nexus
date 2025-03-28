package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.misc;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.ISupportPlants;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.ICompostable;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.ICustomNoteBlock;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import org.bukkit.Instrument;
import org.bukkit.Material;

@CustomBlockConfig(
	name = "Flowering Moss Block",
	itemModel = ItemModelType.MISC_FLOWERING_MOSS_BLOCK
)
@CustomNoteBlockConfig(
	instrument = Instrument.BASS_GUITAR,
	step = 19,
	customBreakSound = "block.moss.break",
	customPlaceSound = "block.moss.place",
	customStepSound = "block.moss.step",
	customHitSound = "block.moss.hit",
	customFallSound = "block.moss.fall"
)

public class FloweringMossBlock implements ICustomNoteBlock, ICompostable, ISupportPlants {
	public static final int FERTILIZE_CHANCE = 10;

	@Override
	public double getBlockHardness() {
		return 0.1;
	}

	@Override
	public Material getMinimumPreferredTool() {
		return Material.WOODEN_HOE;
	}

	@Override
	public PistonAction getPistonPushAction() {
		return PistonAction.BREAK;
	}

	@Override
	public PistonAction getPistonPullAction() {
		return PistonAction.PREVENT;
	}

	@Override
	public int getCompostChance() {
		return 65;
	}
}
