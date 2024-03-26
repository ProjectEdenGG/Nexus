package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.colored;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
		name = "Magenta Planks",
		modelId = 20160
)
@CustomNoteBlockConfig(
		instrument = Instrument.BELL,
		step = 10
)
public class MagentaPlanks implements IColoredPlanks {
}
