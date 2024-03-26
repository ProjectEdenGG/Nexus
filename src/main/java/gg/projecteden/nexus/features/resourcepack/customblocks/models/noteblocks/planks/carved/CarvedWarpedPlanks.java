package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.carved;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
		name = "Carved Warped Planks",
		modelId = 20016
)
@CustomNoteBlockConfig(
		instrument = Instrument.BANJO,
		step = 16
)
public class CarvedWarpedPlanks implements ICarvedPlanks {
}
