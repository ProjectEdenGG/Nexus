package gg.projecteden.nexus.features.customblocks.models.noteblocks.quiltedwool;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Magenta Quilted Wool",
	modelId = 20310
)
@CustomNoteBlockConfig(
	instrument = Instrument.COW_BELL,
	step = 10
)
public class MagentaQuiltedWool implements IQuiltedWool {}
