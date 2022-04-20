package gg.projecteden.nexus.features.customblocks.models.blocks.planks.colored;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.dyeable.IColoredPlanks;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Orange Planks",
	modelId = 20152,
	instrument = Instrument.BELL,
	step = 2
)
public class OrangePlanks implements IColoredPlanks {}
