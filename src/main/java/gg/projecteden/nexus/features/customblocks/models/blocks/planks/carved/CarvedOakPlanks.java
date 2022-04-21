package gg.projecteden.nexus.features.customblocks.models.blocks.planks.carved;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Carved Oak Planks",
	modelId = 20002,
	instrument = Instrument.BANJO,
	step = 2
)
public class CarvedOakPlanks implements ICarvedPlanks {}
