package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.vertical;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
		name = "Vertical Mangrove Planks",
		modelId = 20017
)
@CustomNoteBlockConfig(
		instrument = Instrument.BANJO,
		step = 17
)
public class VerticalMangrovePlanks implements IVerticalPlanks {
}
