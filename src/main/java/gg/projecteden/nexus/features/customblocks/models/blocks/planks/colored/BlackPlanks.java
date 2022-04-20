package gg.projecteden.nexus.features.customblocks.models.blocks.planks.colored;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.IColoredPlanks;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Black Planks",
	modelId = 20163,
	instrument = Instrument.BELL,
	step = 13
)
public class BlackPlanks implements IColoredPlanks {}
