package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.vertical;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Vertical Birch Planks",
	itemModel = ItemModelType.WOOD_BIRCH_VERTICAL_BIRCH_PLANKS
)
@CustomNoteBlockConfig(
	instrument = Instrument.BANJO,
	step = 5
)
public class VerticalBirchPlanks implements IVerticalPlanks {
}
