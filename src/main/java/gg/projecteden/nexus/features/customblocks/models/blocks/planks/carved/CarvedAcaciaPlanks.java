package gg.projecteden.nexus.features.customblocks.models.blocks.planks.carved;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Carved Acacia Planks",
	modelId = 20010,
	instrument = Instrument.BANJO,
	step = 10
)
public class CarvedAcaciaPlanks implements ICarvedPlanks {}
