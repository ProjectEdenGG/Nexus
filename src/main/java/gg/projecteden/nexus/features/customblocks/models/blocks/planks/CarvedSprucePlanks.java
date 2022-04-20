package gg.projecteden.nexus.features.customblocks.models.blocks.planks;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.ICarvedPlanks;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Carved Spruce Planks",
	modelId = 20004,
	instrument = Instrument.BANJO,
	step = 4
)
public class CarvedSprucePlanks implements ICarvedPlanks {}
