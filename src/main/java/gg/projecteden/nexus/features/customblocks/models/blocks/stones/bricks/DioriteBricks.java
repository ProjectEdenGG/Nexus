package gg.projecteden.nexus.features.customblocks.models.blocks.stones.bricks;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.IStoneBricks;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Diorite Bricks",
	modelId = 20353,
	instrument = Instrument.DIDGERIDOO,
	step = 4
)
public class DioriteBricks implements IStoneBricks {}
