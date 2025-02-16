package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.carved;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Carved Mangrove Planks",
	itemModel = ItemModelType.WOOD_MANGROVE_CARVED_MANGROVE_PLANKS
)
@CustomNoteBlockConfig(
	instrument = Instrument.BANJO,
	step = 18
)
public class CarvedMangrovePlanks implements ICarvedPlanks {
}
