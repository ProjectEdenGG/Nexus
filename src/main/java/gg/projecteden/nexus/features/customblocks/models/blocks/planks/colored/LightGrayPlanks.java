package gg.projecteden.nexus.features.customblocks.models.blocks.planks.colored;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.dyeable.IColoredPlanks;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Light Gray Planks",
	modelId = 20165,
	instrument = Instrument.BELL,
	step = 15
)
public class LightGrayPlanks implements IColoredPlanks {}
