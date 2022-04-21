package gg.projecteden.nexus.features.customblocks.models.blocks.planks.carved;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Carved Warped Planks",
	modelId = 20016,
	instrument = Instrument.BANJO,
	step = 16
)
public class CarvedWarpedPlanks implements ICarvedPlanks {}
