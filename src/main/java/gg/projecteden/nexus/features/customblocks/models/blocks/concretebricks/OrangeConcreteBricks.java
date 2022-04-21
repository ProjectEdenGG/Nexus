package gg.projecteden.nexus.features.customblocks.models.blocks.concretebricks;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Magenta Concrete Bricks",
	modelId = 20252,
	instrument = Instrument.CHIME,
	step = 2
)
public class OrangeConcreteBricks implements IConcreteBricks {}
