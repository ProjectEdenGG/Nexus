package gg.projecteden.nexus.features.customblocks.models.blocks.concrete_bricks;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.dyeable.IConcreteBricks;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Purple Concrete Bricks",
	modelId = 20259,
	instrument = Instrument.CHIME,
	step = 9
)
public class PurpleConcreteBricks implements IConcreteBricks {}
