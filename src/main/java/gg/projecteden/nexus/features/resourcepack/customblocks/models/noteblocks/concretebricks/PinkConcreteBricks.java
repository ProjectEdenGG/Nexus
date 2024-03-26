package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.concretebricks;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Pink Concrete Bricks",
	modelId = 20261
)
@CustomNoteBlockConfig(
	instrument = Instrument.CHIME,
	step = 11,
	customBreakSound = "custom.block.stone.break",
	customPlaceSound = "custom.block.stone.place",
	customStepSound = "custom.block.stone.step",
	customHitSound = "custom.block.stone.hit",
	customFallSound = "custom.block.stone.fall"
)
public class PinkConcreteBricks implements IConcreteBricks {}
