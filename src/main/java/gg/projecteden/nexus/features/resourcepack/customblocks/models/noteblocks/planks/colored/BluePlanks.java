package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.colored;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Blue Planks",
	itemModel = ItemModelType.WOOD_COLORED_BLUE
)
@CustomNoteBlockConfig(
	instrument = Instrument.BELL,
	step = 8
)
public class BluePlanks implements IColoredPlanks {
}
