package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.stones.bricks;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Diorite Bricks",
	itemModel = ItemModelType.STONES_GRANITE_GRANITE_BRICKS
)
@CustomNoteBlockConfig(
	instrument = Instrument.DIDGERIDOO,
	step = 9,
	customBreakSound = "custom.block.stone.break",
	customPlaceSound = "custom.block.stone.place",
	customStepSound = "custom.block.stone.step",
	customHitSound = "custom.block.stone.hit",
	customFallSound = "custom.block.stone.fall"
)
public class GraniteBricks implements IStoneBricks {}
