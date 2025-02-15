package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.carved;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Carved Jungle Planks",
	itemModel = ItemModelType.WOOD_JUNGLE_CARVED_JUNGLE_PLANKS
)
@CustomNoteBlockConfig(
	instrument = Instrument.BANJO,
	step = 8
)
public class CarvedJunglePlanks implements ICarvedPlanks {
}
