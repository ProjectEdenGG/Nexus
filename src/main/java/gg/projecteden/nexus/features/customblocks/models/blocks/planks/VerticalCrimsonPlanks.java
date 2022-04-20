package gg.projecteden.nexus.features.customblocks.models.blocks.planks;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.IVerticalPlanks;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Vertical Crimson Planks",
	modelId = 20013,
	instrument = Instrument.BANJO,
	step = 13
)
public class VerticalCrimsonPlanks implements IVerticalPlanks {}
