package gg.projecteden.nexus.features.customblocks.models.blocks.planks.vertical;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Vertical Birch Planks",
	modelId = 20005,
	instrument = Instrument.BANJO,
	step = 5
)
public class VerticalBirchPlanks implements IVerticalPlanks {}
