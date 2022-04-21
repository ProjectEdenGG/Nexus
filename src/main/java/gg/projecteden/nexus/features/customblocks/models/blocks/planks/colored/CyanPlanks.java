package gg.projecteden.nexus.features.customblocks.models.blocks.planks.colored;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Cyan Planks",
	modelId = 20156,
	instrument = Instrument.BELL,
	step = 6
)
public class CyanPlanks implements IColoredPlanks {}
