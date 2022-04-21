package gg.projecteden.nexus.features.customblocks.models.blocks.planks.vertical;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Vertical Oak Planks",
	modelId = 20001,
	instrument = Instrument.BANJO,
	step = 1
)
public class VerticalOakPlanks implements IVerticalPlanks {}
