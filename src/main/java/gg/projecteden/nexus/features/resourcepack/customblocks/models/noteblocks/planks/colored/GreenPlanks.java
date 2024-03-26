package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.colored;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
		name = "Green Planks",
		modelId = 20155
)
@CustomNoteBlockConfig(
		instrument = Instrument.BELL,
		step = 5
)
public class GreenPlanks implements IColoredPlanks {
}
