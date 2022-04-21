package gg.projecteden.nexus.features.customblocks.models.blocks.planks.colored;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Blue Planks",
	modelId = 20158,
	instrument = Instrument.BELL,
	step = 8
)
public class BluePlanks implements IColoredPlanks {}
