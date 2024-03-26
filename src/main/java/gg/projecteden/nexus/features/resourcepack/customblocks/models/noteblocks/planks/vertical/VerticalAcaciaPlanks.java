package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.vertical;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
		name = "Vertical Acacia Planks",
		modelId = 20009
)
@CustomNoteBlockConfig(
		instrument = Instrument.BANJO,
		step = 9
)
public class VerticalAcaciaPlanks implements IVerticalPlanks {
}
