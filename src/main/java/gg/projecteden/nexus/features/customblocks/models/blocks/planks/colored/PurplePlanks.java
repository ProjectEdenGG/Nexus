package gg.projecteden.nexus.features.customblocks.models.blocks.planks.colored;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.IColoredPlanks;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Purple Planks",
	modelId = 20159,
	instrument = Instrument.BELL,
	step = 9
)
public class PurplePlanks implements IColoredPlanks {}
