package gg.projecteden.nexus.features.customblocks.models.blocks.stones.chiseled;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.IChiseledBricks;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Chiseled Diorite",
	modelId = 20354,
	instrument = Instrument.DIDGERIDOO,
	step = 5
)
public class ChiseledDiorite implements IChiseledBricks {}
