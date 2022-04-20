package gg.projecteden.nexus.features.customblocks.models.blocks.concretebricks;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.dyeable.IConcreteBricks;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Red Concrete Bricks",
	modelId = 20251,
	instrument = Instrument.CHIME,
	step = 1
)
public class RedConcreteBricks implements IConcreteBricks {}
