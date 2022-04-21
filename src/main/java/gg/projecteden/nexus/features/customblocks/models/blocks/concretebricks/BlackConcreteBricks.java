package gg.projecteden.nexus.features.customblocks.models.blocks.concretebricks;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Black Concrete Bricks",
	modelId = 20263,
	instrument = Instrument.CHIME,
	step = 13
)
public class BlackConcreteBricks implements IConcreteBricks {}
