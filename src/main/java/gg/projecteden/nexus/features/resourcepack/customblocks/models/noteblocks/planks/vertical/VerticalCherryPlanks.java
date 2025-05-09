package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.vertical;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Vertical Cherry Planks",
	itemModel = ItemModelType.WOOD_CHERRY_VERTICAL_CHERRY_PLANKS
)
@CustomNoteBlockConfig(
	instrument = Instrument.BANJO,
	step = 19
)
public class VerticalCherryPlanks implements IVerticalPlanks {
}
