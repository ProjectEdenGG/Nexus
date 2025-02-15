package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.vertical;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Vertical Oak Planks",
	itemModel = ItemModelType.WOOD_OAK_VERTICAL_OAK_PLANKS
)
@CustomNoteBlockConfig(
	instrument = Instrument.BANJO,
	step = 1
)
public class VerticalOakPlanks implements IVerticalPlanks {
}
