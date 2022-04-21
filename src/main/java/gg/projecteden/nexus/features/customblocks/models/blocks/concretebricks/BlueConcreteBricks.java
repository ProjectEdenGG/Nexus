package gg.projecteden.nexus.features.customblocks.models.blocks.concretebricks;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Blue Concrete Bricks",
	modelId = 20258,
	instrument = Instrument.CHIME,
	step = 8
)
public class BlueConcreteBricks implements IConcreteBricks {}
