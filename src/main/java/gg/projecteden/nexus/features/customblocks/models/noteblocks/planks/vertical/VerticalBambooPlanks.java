package gg.projecteden.nexus.features.customblocks.models.noteblocks.planks.vertical;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Vertical Bamboo Planks",
	modelId = 20021
)
@CustomNoteBlockConfig(
	instrument = Instrument.BANJO,
	step = 21
)
public class VerticalBambooPlanks implements IVerticalPlanks {
}
