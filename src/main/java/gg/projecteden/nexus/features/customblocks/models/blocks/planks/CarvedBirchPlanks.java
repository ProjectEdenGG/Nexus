package gg.projecteden.nexus.features.customblocks.models.blocks.planks;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.ICarvedPlanks;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Carved Birch Planks",
	modelId = 20006,
	instrument = Instrument.BANJO,
	step = 6
)
public class CarvedBirchPlanks implements ICarvedPlanks {}
