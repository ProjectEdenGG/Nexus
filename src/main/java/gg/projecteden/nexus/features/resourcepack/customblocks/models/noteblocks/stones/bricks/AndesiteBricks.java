package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.stones.bricks;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Andesite Bricks",
	itemModel = ItemModelType.STONES_ANDESITE_ANDESITE_BRICKS
)
@CustomNoteBlockConfig(
	instrument = Instrument.DIDGERIDOO,
	step = 3,
	customBreakSound = "custom.block.stone.break",
	customPlaceSound = "custom.block.stone.place",
	customStepSound = "custom.block.stone.step",
	customHitSound = "custom.block.stone.hit",
	customFallSound = "custom.block.stone.fall"
)
public class AndesiteBricks implements IStoneBricks {}
