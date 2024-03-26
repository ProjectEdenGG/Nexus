package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.carved;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
		name = "Carved Dark Oak Planks",
		modelId = 20012
)
@CustomNoteBlockConfig(
		instrument = Instrument.BANJO,
		step = 12
)
public class CarvedDarkOakPlanks implements ICarvedPlanks {
}
