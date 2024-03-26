package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.vertical;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
		name = "Vertical Crimson Planks",
		modelId = 20013
)
@CustomNoteBlockConfig(
		instrument = Instrument.BANJO,
		step = 13
)
public class VerticalCrimsonPlanks implements IVerticalPlanks {
}
