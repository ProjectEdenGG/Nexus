package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.carved;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Carved Warped Planks",
	material = CustomMaterial.WOOD_WARPED_CARVED_WARPED_PLANKS
)
@CustomNoteBlockConfig(
	instrument = Instrument.BANJO,
	step = 16
)
public class CarvedWarpedPlanks implements ICarvedPlanks {
}
