package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.carved;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Carved Acacia Planks",
	itemModel = ItemModelType.WOOD_ACACIA_CARVED_ACACIA_PLANKS
)
@CustomNoteBlockConfig(
	instrument = Instrument.BANJO,
	step = 10
)
public class CarvedAcaciaPlanks implements ICarvedPlanks {
}
