package gg.projecteden.nexus.features.customblocks.models.blocks.concretebricks;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Green Concrete Bricks",
	modelId = 20255,
	instrument = Instrument.CHIME,
	step = 5
)
public class GreenConcreteBricks implements IConcreteBricks {}
