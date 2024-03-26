package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.carved;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
		name = "Carved Acacia Planks",
		modelId = 20010
)
@CustomNoteBlockConfig(
		instrument = Instrument.BANJO,
		step = 10
)
public class CarvedAcaciaPlanks implements ICarvedPlanks {
}
