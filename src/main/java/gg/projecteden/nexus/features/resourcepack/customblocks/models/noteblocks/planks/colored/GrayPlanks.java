package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.colored;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
		name = "Gray Planks",
		modelId = 20164
)
@CustomNoteBlockConfig(
		instrument = Instrument.BELL,
		step = 14
)
public class GrayPlanks implements IColoredPlanks {
}
