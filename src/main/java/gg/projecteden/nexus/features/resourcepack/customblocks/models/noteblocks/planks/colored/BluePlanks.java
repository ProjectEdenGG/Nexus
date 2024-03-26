package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.colored;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
		name = "Blue Planks",
		modelId = 20158
)
@CustomNoteBlockConfig(
		instrument = Instrument.BELL,
		step = 8
)
public class BluePlanks implements IColoredPlanks {
}
