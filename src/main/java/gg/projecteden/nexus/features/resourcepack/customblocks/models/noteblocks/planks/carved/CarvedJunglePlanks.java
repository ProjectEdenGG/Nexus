package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.carved;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
		name = "Carved Jungle Planks",
		modelId = 20008
)
@CustomNoteBlockConfig(
		instrument = Instrument.BANJO,
		step = 8
)
public class CarvedJunglePlanks implements ICarvedPlanks {
}
