package gg.projecteden.nexus.features.customblocks.models.blocks.planks;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.ICarvedPlanks;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Carved Dark Oak Planks",
	modelId = 20012,
	instrument = Instrument.BANJO,
	step = 12
)
public class CarvedDarkOakPlanks implements ICarvedPlanks {}
