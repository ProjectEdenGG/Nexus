package gg.projecteden.nexus.features.customblocks.models.noteblocks.quiltedwool;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Green Quilted Wool",
	modelId = 20305
)
@CustomNoteBlockConfig(
	instrument = Instrument.COW_BELL,
	step = 5
)
public class GreenQuiltedWool implements IQuiltedWool {}
