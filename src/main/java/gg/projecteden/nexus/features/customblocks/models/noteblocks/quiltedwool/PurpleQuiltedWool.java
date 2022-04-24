package gg.projecteden.nexus.features.customblocks.models.noteblocks.quiltedwool;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Purple Quilted Wool",
	modelId = 20309
)
@CustomNoteBlockConfig(
	instrument = Instrument.COW_BELL,
	step = 9
)
public class PurpleQuiltedWool implements IQuiltedWool {}
