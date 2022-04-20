package gg.projecteden.nexus.features.customblocks.models.blocks.planks;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.ICarvedPlanks;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Carved Oak Planks",
	modelId = 20002,
	instrument = Instrument.BANJO,
	step = 2
)
public class CarvedOakPlanks implements ICarvedPlanks {}
