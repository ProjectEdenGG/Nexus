package gg.projecteden.nexus.features.customblocks.models.blocks.planks.colored;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Light Blue Planks",
	modelId = 20157,
	instrument = Instrument.BELL,
	step = 7
)
public class LightBluePlanks implements IColoredPlanks {}
