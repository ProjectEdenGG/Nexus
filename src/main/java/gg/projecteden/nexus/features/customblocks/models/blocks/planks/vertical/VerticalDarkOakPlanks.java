package gg.projecteden.nexus.features.customblocks.models.blocks.planks.vertical;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Vertical Dark Oak Planks",
	modelId = 20011,
	instrument = Instrument.BANJO,
	step = 11
)
public class VerticalDarkOakPlanks implements IVerticalPlanks {}
