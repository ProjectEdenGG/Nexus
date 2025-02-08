package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.concretebricks;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Purple Concrete Bricks",
	material = CustomMaterial.CONCRETE_BRICKS_PURPLE
)
@CustomNoteBlockConfig(
	instrument = Instrument.CHIME,
	step = 9,
	customBreakSound = "custom.block.stone.break",
	customPlaceSound = "custom.block.stone.place",
	customStepSound = "custom.block.stone.step",
	customHitSound = "custom.block.stone.hit",
	customFallSound = "custom.block.stone.fall"
)
public class PurpleConcreteBricks implements IConcreteBricks {}
