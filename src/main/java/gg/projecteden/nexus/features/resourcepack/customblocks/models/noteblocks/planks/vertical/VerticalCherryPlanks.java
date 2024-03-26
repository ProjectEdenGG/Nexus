package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.vertical;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
		name = "Vertical Cherry Planks",
		modelId = 20019
)
@CustomNoteBlockConfig(
		instrument = Instrument.BANJO,
		step = 19
)
public class VerticalCherryPlanks implements IVerticalPlanks {
}
