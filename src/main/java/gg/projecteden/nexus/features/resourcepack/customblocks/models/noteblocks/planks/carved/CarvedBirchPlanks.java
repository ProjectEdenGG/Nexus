package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.carved;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Carved Birch Planks",
	itemModel = ItemModelType.WOOD_BIRCH_CARVED_BIRCH_PLANKS
)
@CustomNoteBlockConfig(
	instrument = Instrument.BANJO,
	step = 6
)
public class CarvedBirchPlanks implements ICarvedPlanks {
}
