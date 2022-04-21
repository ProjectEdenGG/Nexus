package gg.projecteden.nexus.features.customblocks.models.blocks.planks.vertical;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Vertical Jungle Planks",
	modelId = 20007,
	instrument = Instrument.BANJO,
	step = 7
)
public class VerticalJunglePlanks implements IVerticalPlanks {}
