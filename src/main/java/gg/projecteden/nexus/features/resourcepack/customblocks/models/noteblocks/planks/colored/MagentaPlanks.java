package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.colored;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Magenta Planks",
	material = CustomMaterial.WOOD_COLORED_MAGENTA
)
@CustomNoteBlockConfig(
	instrument = Instrument.BELL,
	step = 10
)
public class MagentaPlanks implements IColoredPlanks {
}
