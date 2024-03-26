package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.vertical;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
		name = "Vertical Birch Planks",
		modelId = 20005
)
@CustomNoteBlockConfig(
		instrument = Instrument.BANJO,
		step = 5
)
public class VerticalBirchPlanks implements IVerticalPlanks {
}
