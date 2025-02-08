package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.vertical;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Vertical Crimson Planks",
	material = CustomMaterial.WOOD_CRIMSON_VERTICAL_CRIMSON_PLANKS
)
@CustomNoteBlockConfig(
	instrument = Instrument.BANJO,
	step = 13
)
public class VerticalCrimsonPlanks implements IVerticalPlanks {
}
