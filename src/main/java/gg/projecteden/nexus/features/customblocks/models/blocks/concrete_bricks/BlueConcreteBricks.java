package gg.projecteden.nexus.features.customblocks.models.blocks.concrete_bricks;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.dyeable.IConcreteBricks;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Blue Concrete Bricks",
	modelId = 20258,
	instrument = Instrument.CHIME,
	step = 8
)
public class BlueConcreteBricks implements IConcreteBricks {}
