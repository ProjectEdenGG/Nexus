package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.carved;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
		name = "Carved Oak Planks",
		modelId = 20002
)
@CustomNoteBlockConfig(
		instrument = Instrument.BANJO,
		step = 2
)
public class CarvedOakPlanks implements ICarvedPlanks {
}
