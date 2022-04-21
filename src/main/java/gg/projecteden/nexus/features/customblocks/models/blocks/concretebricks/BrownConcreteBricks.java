package gg.projecteden.nexus.features.customblocks.models.blocks.concretebricks;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Brown Concrete Bricks",
	modelId = 20262,
	instrument = Instrument.CHIME,
	step = 12
)
public class BrownConcreteBricks implements IConcreteBricks {}
