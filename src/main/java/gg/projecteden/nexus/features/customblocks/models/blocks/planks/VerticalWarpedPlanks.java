package gg.projecteden.nexus.features.customblocks.models.blocks.planks;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.IVerticalPlanks;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Vertical Warped Planks",
	modelId = 20015,
	instrument = Instrument.BANJO,
	step = 15
)
public class VerticalWarpedPlanks implements IVerticalPlanks {}
