package gg.projecteden.nexus.features.customblocks.models.blocks.concretebricks;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.dyeable.IConcreteBricks;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Gray Concrete Bricks",
	modelId = 20264,
	instrument = Instrument.CHIME,
	step = 14
)
public class GrayConcreteBricks implements IConcreteBricks {}
