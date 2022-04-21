package gg.projecteden.nexus.features.customblocks.models.blocks.concretebricks;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Lime Concrete Bricks",
	modelId = 20254,
	instrument = Instrument.CHIME,
	step = 4
)
public class LimeConcreteBricks implements IConcreteBricks {}
