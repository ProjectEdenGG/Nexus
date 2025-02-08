package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.colored;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Yellow Planks",
	material = CustomMaterial.WOOD_COLORED_YELLOW
)
@CustomNoteBlockConfig(
	instrument = Instrument.BELL,
	step = 3
)
public class YellowPlanks implements IColoredPlanks {
}
