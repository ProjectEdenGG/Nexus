package gg.projecteden.nexus.features.customblocks.models.noteblocks.concretebricks;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Purple Concrete Bricks",
	modelId = 20259
)
@CustomNoteBlockConfig(
	instrument = Instrument.CHIME,
	step = 9
)
public class PurpleConcreteBricks implements IConcreteBricks {}
