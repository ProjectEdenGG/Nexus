package gg.projecteden.nexus.features.customblocks.models.blocks.planks.colored;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.IColoredPlanks;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "White Planks",
	modelId = 20166,
	instrument = Instrument.BELL,
	step = 16
)
public class WhitePlanks implements IColoredPlanks {}
