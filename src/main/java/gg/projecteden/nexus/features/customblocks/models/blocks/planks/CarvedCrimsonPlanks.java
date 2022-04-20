package gg.projecteden.nexus.features.customblocks.models.blocks.planks;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.ICarvedPlanks;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Carved Crimson Planks",
	modelId = 20014,
	instrument = Instrument.BANJO,
	step = 14
)
public class CarvedCrimsonPlanks implements ICarvedPlanks {}
