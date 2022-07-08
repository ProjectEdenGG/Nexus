package gg.projecteden.nexus.features.customblocks.models.noteblocks.planks.vertical;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Vertical Jungle Planks",
	modelId = 20007
)
@CustomNoteBlockConfig(
	instrument = Instrument.BANJO,
	step = 7
)
public class VerticalJunglePlanks implements IVerticalPlanks {}
