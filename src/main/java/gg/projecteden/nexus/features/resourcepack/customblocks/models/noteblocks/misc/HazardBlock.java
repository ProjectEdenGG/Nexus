package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.misc;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.ICustomNoteBlock;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import org.bukkit.Instrument;
import org.bukkit.Material;

@CustomBlockConfig(
	name = "Hazard Block",
	itemModel = ItemModelType.MISC_HAZARD_BLOCK
)
@CustomNoteBlockConfig(
	instrument = Instrument.BASS_GUITAR,
	step = 21,
	customBreakSound = "custom.block.stone.break",
	customPlaceSound = "custom.block.stone.place",
	customStepSound = "custom.block.stone.step",
	customHitSound = "custom.block.stone.hit",
	customFallSound = "custom.block.stone.fall"
)
public class HazardBlock implements ICustomNoteBlock {

	@Override
	public double getBlockHardness() {
		return 2.0;
	}

	@Override
	public Material getMinimumPreferredTool() {
		return Material.WOODEN_PICKAXE;
	}

}
