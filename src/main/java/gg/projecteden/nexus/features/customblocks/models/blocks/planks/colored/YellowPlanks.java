package gg.projecteden.nexus.features.customblocks.models.blocks.planks.colored;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Yellow Planks",
	modelId = 20153,
	instrument = Instrument.BELL,
	step = 3
)
public class YellowPlanks implements IColoredPlanks {}
