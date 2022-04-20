package gg.projecteden.nexus.features.customblocks.models.blocks.planks;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.IVerticalPlanks;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Vertical Acacia Planks",
	modelId = 20009,
	instrument = Instrument.BANJO,
	step = 9
)
public class VerticalAcaciaPlanks implements IVerticalPlanks {}
