package gg.projecteden.nexus.features.customblocks.models.blocks.planks;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.IVerticalPlanks;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Vertical Spruce Planks",
	modelId = 20003,
	instrument = Instrument.BANJO,
	step = 3
)
public class VerticalSprucePlanks implements IVerticalPlanks {}
