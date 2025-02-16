package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.carved;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Carved Dark Oak Planks",
	itemModel = ItemModelType.WOOD_DARK_OAK_CARVED_DARK_OAK_PLANKS
)
@CustomNoteBlockConfig(
	instrument = Instrument.BANJO,
	step = 12
)
public class CarvedDarkOakPlanks implements ICarvedPlanks {
}
