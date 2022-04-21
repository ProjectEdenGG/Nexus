package gg.projecteden.nexus.features.customblocks.models.blocks.stones.chiseled;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Chiseled Diorite",
	modelId = 20354,
	instrument = Instrument.DIDGERIDOO,
	step = 5
)
public class ChiseledDiorite implements IChiseledStone {
}
