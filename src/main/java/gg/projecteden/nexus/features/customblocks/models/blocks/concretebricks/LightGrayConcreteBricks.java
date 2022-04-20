package gg.projecteden.nexus.features.customblocks.models.blocks.concretebricks;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.dyeable.IConcreteBricks;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Light Gray Concrete Bricks",
	modelId = 20265,
	instrument = Instrument.CHIME,
	step = 15
)
public class LightGrayConcreteBricks implements IConcreteBricks {}
