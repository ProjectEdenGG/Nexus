package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.colored;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Red Planks",
	itemModel = ItemModelType.WOOD_COLORED_RED
)
@CustomNoteBlockConfig(
	instrument = Instrument.BELL,
	step = 1
)
public class RedPlanks implements IColoredPlanks {
}
