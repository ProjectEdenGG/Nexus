package gg.projecteden.nexus.features.customblocks.models.blocks.planks.carved;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Carved Spruce Planks",
	modelId = 20004,
	instrument = Instrument.BANJO,
	step = 4
)
public class CarvedSprucePlanks implements ICarvedPlanks {}
