package gg.projecteden.nexus.features.customblocks.models.blocks.planks.colored;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Gray Planks",
	modelId = 20164,
	instrument = Instrument.BELL,
	step = 14
)
public class GrayPlanks implements IColoredPlanks {}
