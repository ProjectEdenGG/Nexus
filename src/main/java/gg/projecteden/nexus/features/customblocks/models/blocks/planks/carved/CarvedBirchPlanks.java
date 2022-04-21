package gg.projecteden.nexus.features.customblocks.models.blocks.planks.carved;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Carved Birch Planks",
	modelId = 20006,
	instrument = Instrument.BANJO,
	step = 6
)
public class CarvedBirchPlanks implements ICarvedPlanks {}
