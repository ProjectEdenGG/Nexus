package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.carved;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
		name = "Carved Birch Planks",
		modelId = 20006
)
@CustomNoteBlockConfig(
		instrument = Instrument.BANJO,
		step = 6
)
public class CarvedBirchPlanks implements ICarvedPlanks {
}
