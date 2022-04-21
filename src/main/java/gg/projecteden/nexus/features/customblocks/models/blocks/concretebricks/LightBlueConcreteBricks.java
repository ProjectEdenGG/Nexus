package gg.projecteden.nexus.features.customblocks.models.blocks.concretebricks;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Light Blue Concrete Bricks",
	modelId = 20257,
	instrument = Instrument.CHIME,
	step = 7
)
public class LightBlueConcreteBricks implements IConcreteBricks {}
