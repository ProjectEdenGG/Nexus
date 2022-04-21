package gg.projecteden.nexus.features.customblocks.models.blocks.concretebricks;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Gray Concrete Bricks",
	modelId = 20264,
	instrument = Instrument.CHIME,
	step = 14
)
public class GrayConcreteBricks implements IConcreteBricks {}
