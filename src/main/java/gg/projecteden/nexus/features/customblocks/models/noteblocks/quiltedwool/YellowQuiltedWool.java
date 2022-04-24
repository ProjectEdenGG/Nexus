package gg.projecteden.nexus.features.customblocks.models.noteblocks.quiltedwool;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Yellow Quilted Wool",
	modelId = 20303
)
@CustomNoteBlockConfig(
	instrument = Instrument.COW_BELL,
	step = 3
)
public class YellowQuiltedWool implements IQuiltedWool {}
