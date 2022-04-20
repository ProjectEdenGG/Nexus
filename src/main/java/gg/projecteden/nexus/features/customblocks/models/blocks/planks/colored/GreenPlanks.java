package gg.projecteden.nexus.features.customblocks.models.blocks.planks.colored;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.IColoredPlanks;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Green Planks",
	modelId = 20155,
	instrument = Instrument.BELL,
	step = 5
)
public class GreenPlanks implements IColoredPlanks {}
