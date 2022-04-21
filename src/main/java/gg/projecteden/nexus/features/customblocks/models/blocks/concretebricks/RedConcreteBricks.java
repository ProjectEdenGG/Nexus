package gg.projecteden.nexus.features.customblocks.models.blocks.concretebricks;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Red Concrete Bricks",
	modelId = 20251,
	instrument = Instrument.CHIME,
	step = 1
)
public class RedConcreteBricks implements IConcreteBricks {}
