package gg.projecteden.nexus.features.customblocks.models.blocks.planks.colored;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Magenta Planks",
	modelId = 20160,
	instrument = Instrument.BELL,
	step = 10
)
public class MagentaPlanks implements IColoredPlanks {}
