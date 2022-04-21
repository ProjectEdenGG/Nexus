package gg.projecteden.nexus.features.customblocks.models.blocks.planks.colored;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Red Planks",
	modelId = 20151,
	instrument = Instrument.BELL,
	step = 1
)
public class RedPlanks implements IColoredPlanks {}
