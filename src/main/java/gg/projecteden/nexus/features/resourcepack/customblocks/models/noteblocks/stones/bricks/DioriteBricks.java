package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.stones.bricks;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Diorite Bricks",
	itemModel = ItemModelType.STONES_DIORITE_DIORITE_BRICKS
)
@CustomNoteBlockConfig(
	instrument = Instrument.DIDGERIDOO,
	step = 6,
	customBreakSound = "custom.block.stone.break",
	customPlaceSound = "custom.block.stone.place",
	customStepSound = "custom.block.stone.step",
	customHitSound = "custom.block.stone.hit",
	customFallSound = "custom.block.stone.fall"
)
public class DioriteBricks implements IStoneBricks {}
