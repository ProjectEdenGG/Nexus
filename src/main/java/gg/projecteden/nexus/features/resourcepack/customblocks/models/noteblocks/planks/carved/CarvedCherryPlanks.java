package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.carved;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Carved Cherry Planks",
	itemModel = ItemModelType.WOOD_CHERRY_CARVED_CHERRY_PLANKS
)
@CustomNoteBlockConfig(
	instrument = Instrument.BANJO,
	step = 20
)
public class CarvedCherryPlanks implements ICarvedPlanks {
}
