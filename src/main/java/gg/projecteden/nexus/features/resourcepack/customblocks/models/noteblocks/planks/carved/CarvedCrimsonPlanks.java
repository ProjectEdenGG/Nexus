package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.carved;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
		name = "Carved Crimson Planks",
		modelId = 20014
)
@CustomNoteBlockConfig(
		instrument = Instrument.BANJO,
		step = 14
)
public class CarvedCrimsonPlanks implements ICarvedPlanks {
}
