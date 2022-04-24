package gg.projecteden.nexus.features.customblocks.models.noteblocks.quiltedwool;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Pink Quilted Wool",
	modelId = 20311
)
@CustomNoteBlockConfig(
	instrument = Instrument.COW_BELL,
	step = 11
)
public class PinkQuiltedWool implements IQuiltedWool {}
