package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.colored;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Orange Planks",
	itemModel = ItemModelType.WOOD_COLORED_ORANGE
)
@CustomNoteBlockConfig(
	instrument = Instrument.BELL,
	step = 2
)
public class OrangePlanks implements IColoredPlanks {
}
