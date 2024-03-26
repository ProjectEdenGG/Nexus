package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.colored;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
		name = "Light Gray Planks",
		modelId = 20165
)
@CustomNoteBlockConfig(
		instrument = Instrument.BELL,
		step = 15
)
public class LightGrayPlanks implements IColoredPlanks {
}
