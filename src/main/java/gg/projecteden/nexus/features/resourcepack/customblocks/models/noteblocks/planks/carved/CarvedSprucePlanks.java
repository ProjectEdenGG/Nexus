package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.carved;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
		name = "Carved Spruce Planks",
		modelId = 20004
)
@CustomNoteBlockConfig(
		instrument = Instrument.BANJO,
		step = 4
)
public class CarvedSprucePlanks implements ICarvedPlanks {
}
