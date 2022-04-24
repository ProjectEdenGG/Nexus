package gg.projecteden.nexus.features.customblocks.models.noteblocks.quiltedwool;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "White Quilted Wool",
	modelId = 20316
)
@CustomNoteBlockConfig(
	instrument = Instrument.COW_BELL,
	step = 16
)
public class WhiteQuiltedWool implements IQuiltedWool {}
