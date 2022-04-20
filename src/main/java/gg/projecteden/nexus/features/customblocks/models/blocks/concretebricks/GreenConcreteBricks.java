package gg.projecteden.nexus.features.customblocks.models.blocks.concretebricks;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.dyeable.IConcreteBricks;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Green Concrete Bricks",
	modelId = 20255,
	instrument = Instrument.CHIME,
	step = 5
)
public class GreenConcreteBricks implements IConcreteBricks {}
