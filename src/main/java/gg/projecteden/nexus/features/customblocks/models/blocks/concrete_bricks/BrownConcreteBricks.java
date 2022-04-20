package gg.projecteden.nexus.features.customblocks.models.blocks.concrete_bricks;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.dyeable.IConcreteBricks;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Brown Concrete Bricks",
	modelId = 20262,
	instrument = Instrument.CHIME,
	step = 12
)
public class BrownConcreteBricks implements IConcreteBricks {}
