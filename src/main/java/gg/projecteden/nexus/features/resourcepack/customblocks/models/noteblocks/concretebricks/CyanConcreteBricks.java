package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.concretebricks;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Brown Concrete Bricks",
	modelId = 20256
)
@CustomNoteBlockConfig(
	instrument = Instrument.CHIME,
	step = 6,
	customBreakSound = "custom.block.stone.break",
	customPlaceSound = "custom.block.stone.place",
	customStepSound = "custom.block.stone.step",
	customHitSound = "custom.block.stone.hit",
	customFallSound = "custom.block.stone.fall"
)
public class CyanConcreteBricks implements IConcreteBricks {}
