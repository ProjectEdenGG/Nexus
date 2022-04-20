package gg.projecteden.nexus.features.customblocks.models.blocks.planks;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.ICarvedPlanks;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Carved Acacia Planks",
	modelId = 20010,
	instrument = Instrument.BANJO,
	step = 10
)
public class CarvedAcaciaPlanks implements ICarvedPlanks {}
