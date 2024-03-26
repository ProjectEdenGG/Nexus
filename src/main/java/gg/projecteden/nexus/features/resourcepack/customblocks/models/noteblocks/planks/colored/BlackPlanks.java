package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.colored;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
		name = "Black Planks",
		modelId = 20163
)
@CustomNoteBlockConfig(
		instrument = Instrument.BELL,
		step = 13
)
public class BlackPlanks implements IColoredPlanks {
}
