package gg.projecteden.nexus.features.customblocks.models.blocks.planks.colored;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.IColoredPlanks;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Brown Planks",
	modelId = 20162,
	instrument = Instrument.BELL,
	step = 12
)
public class BrownPlanks implements IColoredPlanks {}
