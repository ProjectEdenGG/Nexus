package gg.projecteden.nexus.features.customblocks.models.noteblocks.stones.bricks;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Diorite Bricks",
	modelId = 20355
)
@CustomNoteBlockConfig(
	instrument = Instrument.DIDGERIDOO,
	step = 6,
	customBreakSound = "block.stone.break",
	customPlaceSound = "block.stone.place",
	customStepSound = "block.stone.step",
	customHitSound = "block.stone.hit",
	customFallSound = "block.stone.fall"
)
public class DioriteBricks implements IStoneBricks {}
