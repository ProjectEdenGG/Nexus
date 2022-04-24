package gg.projecteden.nexus.features.customblocks.models.noteblocks.quiltedwool;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Blue Quilted Wool",
	modelId = 20308
)
@CustomNoteBlockConfig(
	instrument = Instrument.COW_BELL,
	step = 8
)
public class BlueQuiltedWool implements IQuiltedWool {}
