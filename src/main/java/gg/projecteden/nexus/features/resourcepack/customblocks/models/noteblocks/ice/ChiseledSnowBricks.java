package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.ice;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.ICustomNoteBlock;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Chiseled Snow Bricks",
	itemModel = ItemModelType.ICE_CHISELED_SNOW_BRICKS
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
public class ChiseledSnowBricks implements ICustomNoteBlock {
}
