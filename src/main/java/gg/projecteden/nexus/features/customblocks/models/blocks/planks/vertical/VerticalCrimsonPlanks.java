package gg.projecteden.nexus.features.customblocks.models.blocks.planks.vertical;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Vertical Crimson Planks",
	modelId = 20013,
	instrument = Instrument.BANJO,
	step = 13
)
public class VerticalCrimsonPlanks implements IVerticalPlanks {}
