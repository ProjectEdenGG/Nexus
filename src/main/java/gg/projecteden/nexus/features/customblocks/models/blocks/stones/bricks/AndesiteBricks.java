package gg.projecteden.nexus.features.customblocks.models.blocks.stones.bricks;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.IStoneBricks;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Andesite Bricks",
	modelId = 20351,
	instrument = Instrument.DIDGERIDOO,
	step = 2
)
public class AndesiteBricks implements IStoneBricks {}
