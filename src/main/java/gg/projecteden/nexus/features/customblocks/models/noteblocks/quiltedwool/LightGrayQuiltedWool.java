package gg.projecteden.nexus.features.customblocks.models.noteblocks.quiltedwool;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Light Gray Quilted Wool",
	modelId = 20315
)
@CustomNoteBlockConfig(
	instrument = Instrument.COW_BELL,
	step = 15
)
public class LightGrayQuiltedWool implements IQuiltedWool {}
