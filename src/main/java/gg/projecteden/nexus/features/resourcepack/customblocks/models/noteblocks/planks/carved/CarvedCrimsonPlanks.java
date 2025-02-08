package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.carved;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Carved Crimson Planks",
	material = CustomMaterial.WOOD_CRIMSON_CARVED_CRIMSON_PLANKS
)
@CustomNoteBlockConfig(
	instrument = Instrument.BANJO,
	step = 14
)
public class CarvedCrimsonPlanks implements ICarvedPlanks {
}
