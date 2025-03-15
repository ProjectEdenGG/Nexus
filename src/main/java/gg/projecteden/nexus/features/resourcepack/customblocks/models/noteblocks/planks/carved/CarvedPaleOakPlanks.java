package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.carved;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Carved Pale Oak Planks",
	itemModel = ItemModelType.WOOD_PALE_OAK_CARVED_PALE_OAK_PLANKS
)
@CustomNoteBlockConfig(
	instrument = Instrument.BANJO,
	step = 22
)
public class CarvedPaleOakPlanks implements ICarvedPlanks {
}
