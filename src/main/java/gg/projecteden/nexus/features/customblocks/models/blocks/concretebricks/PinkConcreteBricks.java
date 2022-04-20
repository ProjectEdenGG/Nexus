package gg.projecteden.nexus.features.customblocks.models.blocks.concretebricks;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.dyeable.IConcreteBricks;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Pink Concrete Bricks",
	modelId = 20261,
	instrument = Instrument.CHIME,
	step = 11
)
public class PinkConcreteBricks implements IConcreteBricks {}
