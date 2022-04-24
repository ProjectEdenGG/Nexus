package gg.projecteden.nexus.features.customblocks.models.noteblocks.concretebricks;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Green Concrete Bricks",
	modelId = 20255
)
@CustomNoteBlockConfig(
	instrument = Instrument.CHIME,
	step = 5
)
public class GreenConcreteBricks implements IConcreteBricks {}
