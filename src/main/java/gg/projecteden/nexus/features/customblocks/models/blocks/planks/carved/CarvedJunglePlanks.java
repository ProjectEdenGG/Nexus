package gg.projecteden.nexus.features.customblocks.models.blocks.planks.carved;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Carved Jungle Planks",
	modelId = 20008,
	instrument = Instrument.BANJO,
	step = 8
)
public class CarvedJunglePlanks implements ICarvedPlanks {}
