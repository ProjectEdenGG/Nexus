package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.carved;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
		name = "Carved Mangrove Planks",
		modelId = 20018
)
@CustomNoteBlockConfig(
		instrument = Instrument.BANJO,
		step = 18
)
public class CarvedMangrovePlanks implements ICarvedPlanks {
}
