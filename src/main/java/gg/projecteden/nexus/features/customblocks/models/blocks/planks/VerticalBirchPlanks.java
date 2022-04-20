package gg.projecteden.nexus.features.customblocks.models.blocks.planks;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.IVerticalPlanks;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Vertical Birch Planks",
	modelId = 20005,
	instrument = Instrument.BANJO,
	step = 5
)
public class VerticalBirchPlanks implements IVerticalPlanks {}
