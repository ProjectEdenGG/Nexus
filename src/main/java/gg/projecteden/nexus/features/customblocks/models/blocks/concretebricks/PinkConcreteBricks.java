package gg.projecteden.nexus.features.customblocks.models.blocks.concretebricks;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Pink Concrete Bricks",
	modelId = 20261,
	instrument = Instrument.CHIME,
	step = 11
)
public class PinkConcreteBricks implements IConcreteBricks {}
