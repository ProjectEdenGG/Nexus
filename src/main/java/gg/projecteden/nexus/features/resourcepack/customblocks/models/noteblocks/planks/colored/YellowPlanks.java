package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.colored;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
		name = "Yellow Planks",
		modelId = 20153
)
@CustomNoteBlockConfig(
		instrument = Instrument.BELL,
		step = 3
)
public class YellowPlanks implements IColoredPlanks {
}
