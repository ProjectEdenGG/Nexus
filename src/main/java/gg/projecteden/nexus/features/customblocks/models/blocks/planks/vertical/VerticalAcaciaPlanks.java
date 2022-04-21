package gg.projecteden.nexus.features.customblocks.models.blocks.planks.vertical;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Vertical Acacia Planks",
	modelId = 20009,
	instrument = Instrument.BANJO,
	step = 9
)
public class VerticalAcaciaPlanks implements IVerticalPlanks {}
