package gg.projecteden.nexus.features.customblocks.models.blocks.planks.colored;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Light Gray Planks",
	modelId = 20165,
	instrument = Instrument.BELL,
	step = 15
)
public class LightGrayPlanks implements IColoredPlanks {}
