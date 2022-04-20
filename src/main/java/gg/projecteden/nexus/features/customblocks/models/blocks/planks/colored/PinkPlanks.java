package gg.projecteden.nexus.features.customblocks.models.blocks.planks.colored;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.IColoredPlanks;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Pink Planks",
	modelId = 20161,
	instrument = Instrument.BELL,
	step = 11
)
public class PinkPlanks implements IColoredPlanks {}
