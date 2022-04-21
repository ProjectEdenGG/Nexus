package gg.projecteden.nexus.features.customblocks.models.blocks.planks.colored;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Lime Planks",
	modelId = 20154,
	instrument = Instrument.BELL,
	step = 4
)
public class LimePlanks implements IColoredPlanks {}
