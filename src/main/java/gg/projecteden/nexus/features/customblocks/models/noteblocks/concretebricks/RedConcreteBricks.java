package gg.projecteden.nexus.features.customblocks.models.noteblocks.concretebricks;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Red Concrete Bricks",
	modelId = 20251
)
@CustomNoteBlockConfig(
	instrument = Instrument.CHIME,
	step = 1
)
public class RedConcreteBricks implements IConcreteBricks {}
