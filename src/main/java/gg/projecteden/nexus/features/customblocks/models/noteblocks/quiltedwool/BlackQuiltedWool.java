package gg.projecteden.nexus.features.customblocks.models.noteblocks.quiltedwool;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Black Quilted Wool",
	modelId = 20313
)
@CustomNoteBlockConfig(
	instrument = Instrument.COW_BELL,
	step = 13
)
public class BlackQuiltedWool implements IQuiltedWool {}
