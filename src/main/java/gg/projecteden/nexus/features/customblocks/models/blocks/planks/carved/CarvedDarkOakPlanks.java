package gg.projecteden.nexus.features.customblocks.models.blocks.planks.carved;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Carved Dark Oak Planks",
	modelId = 20012,
	instrument = Instrument.BANJO,
	step = 12
)
public class CarvedDarkOakPlanks implements ICarvedPlanks {}
