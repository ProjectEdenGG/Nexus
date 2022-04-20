package gg.projecteden.nexus.features.customblocks.models.blocks.concrete_bricks;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.dyeable.IConcreteBricks;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Magenta Concrete Bricks",
	modelId = 20260,
	instrument = Instrument.CHIME,
	step = 10
)
public class MagentaConcreteBricks implements IConcreteBricks {}
