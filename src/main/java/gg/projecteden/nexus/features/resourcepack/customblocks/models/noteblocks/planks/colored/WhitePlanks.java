package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.colored;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
		name = "White Planks",
		modelId = 20166
)
@CustomNoteBlockConfig(
		instrument = Instrument.BELL,
		step = 16
)
public class WhitePlanks implements IColoredPlanks {
}
