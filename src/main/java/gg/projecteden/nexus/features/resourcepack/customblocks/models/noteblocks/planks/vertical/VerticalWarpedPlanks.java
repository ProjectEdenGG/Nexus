package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.vertical;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
		name = "Vertical Warped Planks",
		modelId = 20015
)
@CustomNoteBlockConfig(
		instrument = Instrument.BANJO,
		step = 15
)
public class VerticalWarpedPlanks implements IVerticalPlanks {
}
