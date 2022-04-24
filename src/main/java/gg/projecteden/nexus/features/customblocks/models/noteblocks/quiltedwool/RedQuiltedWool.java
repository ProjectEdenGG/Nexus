package gg.projecteden.nexus.features.customblocks.models.noteblocks.quiltedwool;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Red Quilted Wool",
	modelId = 20301
)
@CustomNoteBlockConfig(
	instrument = Instrument.COW_BELL,
	step = 1
)
public class RedQuiltedWool implements IQuiltedWool {}
