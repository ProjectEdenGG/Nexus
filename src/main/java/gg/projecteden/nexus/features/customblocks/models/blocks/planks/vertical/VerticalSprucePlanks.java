package gg.projecteden.nexus.features.customblocks.models.blocks.planks.vertical;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Vertical Spruce Planks",
	modelId = 20003,
	instrument = Instrument.BANJO,
	step = 3
)
public class VerticalSprucePlanks implements IVerticalPlanks {}
