package gg.projecteden.nexus.features.customblocks.models.blocks.planks.colored;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Orange Planks",
	modelId = 20152,
	instrument = Instrument.BELL,
	step = 2
)
public class OrangePlanks implements IColoredPlanks {}
