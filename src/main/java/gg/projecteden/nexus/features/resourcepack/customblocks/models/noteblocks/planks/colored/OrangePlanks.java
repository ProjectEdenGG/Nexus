package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.colored;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
		name = "Orange Planks",
		modelId = 20152
)
@CustomNoteBlockConfig(
		instrument = Instrument.BELL,
		step = 2
)
public class OrangePlanks implements IColoredPlanks {
}
