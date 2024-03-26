package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.colored;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
		name = "Cyan Planks",
		modelId = 20156
)
@CustomNoteBlockConfig(
		instrument = Instrument.BELL,
		step = 6
)
public class CyanPlanks implements IColoredPlanks {
}
