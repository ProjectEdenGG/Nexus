package gg.projecteden.nexus.features.customblocks.models.blocks.concretebricks;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "White Concrete Bricks",
	modelId = 20266,
	instrument = Instrument.CHIME,
	step = 16
)
public class WhiteConcreteBricks implements IConcreteBricks {}
