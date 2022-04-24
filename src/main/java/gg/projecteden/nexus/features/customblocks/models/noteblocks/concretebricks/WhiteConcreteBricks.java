package gg.projecteden.nexus.features.customblocks.models.noteblocks.concretebricks;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "White Concrete Bricks",
	modelId = 20266
)
@CustomNoteBlockConfig(
	instrument = Instrument.CHIME,
	step = 16
)
public class WhiteConcreteBricks implements IConcreteBricks {}
