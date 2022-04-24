package gg.projecteden.nexus.features.customblocks.models.noteblocks.quiltedwool;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Lime Quilted Wool",
	modelId = 20304
)
@CustomNoteBlockConfig(
	instrument = Instrument.COW_BELL,
	step = 4
)
public class LimeQuiltedWool implements IQuiltedWool {}
