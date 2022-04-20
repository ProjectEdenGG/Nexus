package gg.projecteden.nexus.features.customblocks.models.blocks.planks;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.IVerticalPlanks;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Vertical Oak Planks",
	modelId = 20001,
	instrument = Instrument.BANJO,
	step = 1
)
public class VerticalOakPlanks implements IVerticalPlanks {}
