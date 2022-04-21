package gg.projecteden.nexus.features.customblocks.models.blocks.planks.carved;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Carved Crimson Planks",
	modelId = 20014,
	instrument = Instrument.BANJO,
	step = 14
)
public class CarvedCrimsonPlanks implements ICarvedPlanks {}
