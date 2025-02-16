package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.carved;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Carved Spruce Planks",
	itemModel = ItemModelType.WOOD_SPRUCE_CARVED_SPRUCE_PLANKS
)
@CustomNoteBlockConfig(
	instrument = Instrument.BANJO,
	step = 4
)
public class CarvedSprucePlanks implements ICarvedPlanks {
}
