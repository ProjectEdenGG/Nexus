package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.vertical;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Vertical Warped Planks",
	itemModel = ItemModelType.WOOD_WARPED_VERTICAL_WARPED_PLANKS
)
@CustomNoteBlockConfig(
	instrument = Instrument.BANJO,
	step = 15
)
public class VerticalWarpedPlanks implements IVerticalPlanks {
}
