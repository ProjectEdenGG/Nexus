package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.colored;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
		name = "Brown Planks",
		modelId = 20162
)
@CustomNoteBlockConfig(
		instrument = Instrument.BELL,
		step = 12
)
public class BrownPlanks implements IColoredPlanks {
}
