package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.colored;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Light Gray Planks",
	material = CustomMaterial.WOOD_COLORED_LIGHT_GRAY
)
@CustomNoteBlockConfig(
	instrument = Instrument.BELL,
	step = 15
)
public class LightGrayPlanks implements IColoredPlanks {
}
