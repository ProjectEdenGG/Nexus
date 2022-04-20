package gg.projecteden.nexus.features.customblocks.models.blocks.planks;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.ICarvedPlanks;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Carved Warped Planks",
	modelId = 20016,
	instrument = Instrument.BANJO,
	step = 16
)
public class CarvedWarpedPlanks implements ICarvedPlanks {}
