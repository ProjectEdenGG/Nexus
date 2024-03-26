package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.colored;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
		name = "Purple Planks",
		modelId = 20159
)
@CustomNoteBlockConfig(
		instrument = Instrument.BELL,
		step = 9
)
public class PurplePlanks implements IColoredPlanks {
}
