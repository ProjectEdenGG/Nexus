package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.vertical;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Vertical Pale Oak Planks",
	itemModel = ItemModelType.WOOD_PALE_OAK_VERTICAL_PALE_OAK_PLANKS
)
@CustomNoteBlockConfig(
	instrument = Instrument.BANJO,
	step = 23
)
public class VerticalPaleOakPlanks implements IVerticalPlanks {
}
