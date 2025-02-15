package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.vertical;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Vertical Mangrove Planks",
	itemModel = ItemModelType.WOOD_MANGROVE_VERTICAL_MANGROVE_PLANKS
)
@CustomNoteBlockConfig(
	instrument = Instrument.BANJO,
	step = 17
)
public class VerticalMangrovePlanks implements IVerticalPlanks {
}
