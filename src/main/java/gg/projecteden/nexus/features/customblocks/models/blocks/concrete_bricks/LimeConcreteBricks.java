package gg.projecteden.nexus.features.customblocks.models.blocks.concrete_bricks;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.dyeable.IConcreteBricks;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Lime Concrete Bricks",
	modelId = 20254,
	instrument = Instrument.CHIME,
	step = 4
)
public class LimeConcreteBricks implements IConcreteBricks {}
