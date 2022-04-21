package gg.projecteden.nexus.features.customblocks.models.blocks.stones.chiseled;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Chiseled Andesite",
	modelId = 20352,
	instrument = Instrument.DIDGERIDOO,
	step = 3
)
public class ChiseledAndesite implements IChiseledStone {
}
