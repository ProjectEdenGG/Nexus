package gg.projecteden.nexus.features.customblocks.models.noteblocks.concretebricks;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Blue Concrete Bricks",
	modelId = 20258
)
@CustomNoteBlockConfig(
	instrument = Instrument.CHIME,
	step = 8
)
public class BlueConcreteBricks implements IConcreteBricks {}
