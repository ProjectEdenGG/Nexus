package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.vertical;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Vertical Spruce Planks",
	itemModel = ItemModelType.WOOD_SPRUCE_VERTICAL_SPRUCE_PLANKS
)
@CustomNoteBlockConfig(
	instrument = Instrument.BANJO,
	step = 3
)
public class VerticalSprucePlanks implements IVerticalPlanks {
}
