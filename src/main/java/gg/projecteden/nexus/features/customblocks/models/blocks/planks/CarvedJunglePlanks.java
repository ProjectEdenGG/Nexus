package gg.projecteden.nexus.features.customblocks.models.blocks.planks;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.ICarvedPlanks;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Carved Jungle Planks",
	modelId = 20008,
	instrument = Instrument.BANJO,
	step = 8
)
public class CarvedJunglePlanks implements ICarvedPlanks {}
