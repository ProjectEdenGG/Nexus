package gg.projecteden.nexus.features.customblocks.models.blocks.planks.vertical;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Vertical Warped Planks",
	modelId = 20015,
	instrument = Instrument.BANJO,
	step = 15
)
public class VerticalWarpedPlanks implements IVerticalPlanks {}
