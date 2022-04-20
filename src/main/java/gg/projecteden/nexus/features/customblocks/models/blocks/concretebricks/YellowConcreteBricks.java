package gg.projecteden.nexus.features.customblocks.models.blocks.concretebricks;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.dyeable.IConcreteBricks;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Yellow Concrete Bricks",
	modelId = 20253,
	instrument = Instrument.CHIME,
	step = 3
)
public class YellowConcreteBricks implements IConcreteBricks {}
