package gg.projecteden.nexus.features.customblocks.models.blocks.concrete_bricks;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.dyeable.IConcreteBricks;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "White Concrete Bricks",
	modelId = 20266,
	instrument = Instrument.CHIME,
	step = 16
)
public class WhiteConcreteBricks implements IConcreteBricks {}
