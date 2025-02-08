package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.vertical;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Vertical Bamboo Planks",
	material = CustomMaterial.WOOD_BAMBOO_VERTICAL_BAMBOO_PLANKS
)
@CustomNoteBlockConfig(
	instrument = Instrument.BANJO,
	step = 21
)
public class VerticalBambooPlanks implements IVerticalPlanks {
}
