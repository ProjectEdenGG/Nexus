package gg.projecteden.nexus.features.customblocks.models.blocks.planks;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.IVerticalPlanks;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Vertical Jungle Planks",
	modelId = 20007,
	instrument = Instrument.BANJO,
	step = 7
)
public class VerticalJunglePlanks implements IVerticalPlanks {}
