package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.vertical;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Vertical Dark Oak Planks",
	itemModel = ItemModelType.WOOD_DARK_OAK_VERTICAL_DARK_OAK_PLANKS
)
@CustomNoteBlockConfig(
	instrument = Instrument.BANJO,
	step = 11
)
public class VerticalDarkOakPlanks implements IVerticalPlanks {
}
