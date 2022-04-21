package gg.projecteden.nexus.features.customblocks.models.blocks.concretebricks;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Purple Concrete Bricks",
	modelId = 20259,
	instrument = Instrument.CHIME,
	step = 9
)
public class PurpleConcreteBricks implements IConcreteBricks {}
