package gg.projecteden.nexus.features.customblocks.models.blocks.concretebricks;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.dyeable.IConcreteBricks;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Black Concrete Bricks",
	modelId = 20263,
	instrument = Instrument.CHIME,
	step = 13
)
public class BlackConcreteBricks implements IConcreteBricks {}
