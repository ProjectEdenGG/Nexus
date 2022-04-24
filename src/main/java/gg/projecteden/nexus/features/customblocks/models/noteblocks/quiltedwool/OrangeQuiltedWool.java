package gg.projecteden.nexus.features.customblocks.models.noteblocks.quiltedwool;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Orange Quilted Wool",
	modelId = 20302
)
@CustomNoteBlockConfig(
	instrument = Instrument.COW_BELL,
	step = 2
)
public class OrangeQuiltedWool implements IQuiltedWool {}
