package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.concretebricks;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Red Concrete Bricks",
	itemModel = ItemModelType.CONCRETE_BRICKS_RED
)
@CustomNoteBlockConfig(
	instrument = Instrument.CHIME,
	step = 1,
	customBreakSound = "custom.block.stone.break",
	customPlaceSound = "custom.block.stone.place",
	customStepSound = "custom.block.stone.step",
	customHitSound = "custom.block.stone.hit",
	customFallSound = "custom.block.stone.fall"
)
public class RedConcreteBricks implements IConcreteBricks {}
