package gg.projecteden.nexus.features.customblocks.models.noteblocks.quiltedwool;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Cyan Quilted Wool",
	modelId = 20306
)
@CustomNoteBlockConfig(
	instrument = Instrument.COW_BELL,
	step = 6
)
public class CyanQuiltedWool implements IQuiltedWool {}
