package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.carved;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
		name = "Carved Cherry Planks",
		modelId = 20020
)
@CustomNoteBlockConfig(
		instrument = Instrument.BANJO,
		step = 19
)
public class CarvedCherryPlanks implements ICarvedPlanks {
}
