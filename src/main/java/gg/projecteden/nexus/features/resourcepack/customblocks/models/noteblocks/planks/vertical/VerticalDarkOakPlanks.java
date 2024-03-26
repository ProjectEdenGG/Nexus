package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.vertical;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
		name = "Vertical Dark Oak Planks",
		modelId = 20011
)
@CustomNoteBlockConfig(
		instrument = Instrument.BANJO,
		step = 11
)
public class VerticalDarkOakPlanks implements IVerticalPlanks {
}
