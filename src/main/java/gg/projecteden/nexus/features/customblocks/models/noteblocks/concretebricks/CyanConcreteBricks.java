package gg.projecteden.nexus.features.customblocks.models.noteblocks.concretebricks;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Brown Concrete Bricks",
	modelId = 20256
)
@CustomNoteBlockConfig(
	instrument = Instrument.CHIME,
	step = 6,
	customBreakSound = "block.stone.break",
	customPlaceSound = "block.stone.place",
	customStepSound = "block.stone.step",
	customHitSound = "block.stone.hit",
	customFallSound = "block.stone.fall"
)
public class CyanConcreteBricks implements IConcreteBricks {}
