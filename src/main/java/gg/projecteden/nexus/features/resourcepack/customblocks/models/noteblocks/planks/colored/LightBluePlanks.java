package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.colored;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
		name = "Light Blue Planks",
		modelId = 20157
)
@CustomNoteBlockConfig(
		instrument = Instrument.BELL,
		step = 7
)
public class LightBluePlanks implements IColoredPlanks {
}
