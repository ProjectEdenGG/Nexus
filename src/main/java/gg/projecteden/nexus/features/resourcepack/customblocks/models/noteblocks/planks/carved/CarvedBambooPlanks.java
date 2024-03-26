package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.carved;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
		name = "Carved Bamboo Planks",
		modelId = 20022
)
@CustomNoteBlockConfig(
		instrument = Instrument.BANJO,
		step = 22
)
public class CarvedBambooPlanks implements ICarvedPlanks {
}
