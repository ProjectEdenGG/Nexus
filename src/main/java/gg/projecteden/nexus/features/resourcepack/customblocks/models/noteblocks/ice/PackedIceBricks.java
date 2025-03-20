package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.ice;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.ICustomNoteBlock;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Packed Ice Bricks",
	itemModel = ItemModelType.ICE_PACKED_ICE_BRICKS
)
@CustomNoteBlockConfig(
	instrument = Instrument.BANJO,
	step = 4,
	powered = true,
	customBreakSound = "block.glass.break",
	customPlaceSound = "block.glass.place",
	customStepSound = "block.glass.step",
	customHitSound = "block.glass.hit",
	customFallSound = "block.glass.fall"
)
public class PackedIceBricks implements ICustomNoteBlock {
}
