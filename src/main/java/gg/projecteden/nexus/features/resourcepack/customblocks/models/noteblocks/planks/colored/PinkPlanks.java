package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.colored;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
		name = "Pink Planks",
		modelId = 20161
)
@CustomNoteBlockConfig(
		instrument = Instrument.BELL,
		step = 11
)
public class PinkPlanks implements IColoredPlanks {
}
