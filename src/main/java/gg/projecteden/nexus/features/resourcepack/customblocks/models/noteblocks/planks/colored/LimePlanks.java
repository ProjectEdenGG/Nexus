package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.colored;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
		name = "Lime Planks",
		modelId = 20154
)
@CustomNoteBlockConfig(
		instrument = Instrument.BELL,
		step = 4
)
public class LimePlanks implements IColoredPlanks {
}
