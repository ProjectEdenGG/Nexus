package gg.projecteden.nexus.features.customblocks.models.noteblocks.quiltedwool;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Light Blue Quilted Wool",
	modelId = 20307
)
@CustomNoteBlockConfig(
	instrument = Instrument.COW_BELL,
	step = 7
)
public class LightBlueQuiltedWool implements IQuiltedWool {}
