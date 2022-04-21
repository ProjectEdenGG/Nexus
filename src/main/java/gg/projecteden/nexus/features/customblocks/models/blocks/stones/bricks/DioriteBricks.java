package gg.projecteden.nexus.features.customblocks.models.blocks.stones.bricks;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Diorite Bricks",
	modelId = 20353,
	instrument = Instrument.DIDGERIDOO,
	step = 4
)
public class DioriteBricks implements IStoneBricks {}
