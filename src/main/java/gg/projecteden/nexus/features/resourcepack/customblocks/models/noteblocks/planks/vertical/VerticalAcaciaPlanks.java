package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.vertical;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Vertical Acacia Planks",
	material = CustomMaterial.WOOD_ACACIA_VERTICAL_ACACIA_PLANKS
)
@CustomNoteBlockConfig(
	instrument = Instrument.BANJO,
	step = 9
)
public class VerticalAcaciaPlanks implements IVerticalPlanks {
}
