package gg.projecteden.nexus.features.customblocks.models.blocks.planks;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.IVerticalPlanks;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Vertical Dark Oak Planks",
	modelId = 20011,
	instrument = Instrument.BANJO,
	step = 11
)
public class VerticalDarkOakPlanks implements IVerticalPlanks {}
