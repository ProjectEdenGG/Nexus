package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.vertical;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
		name = "Vertical Spruce Planks",
		modelId = 20003
)
@CustomNoteBlockConfig(
		instrument = Instrument.BANJO,
		step = 3
)
public class VerticalSprucePlanks implements IVerticalPlanks {
}
