package gg.projecteden.nexus.features.customblocks.models.noteblocks.concretebricks;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Yellow Concrete Bricks",
	modelId = 20253
)
@CustomNoteBlockConfig(
	instrument = Instrument.CHIME,
	step = 3
)
public class YellowConcreteBricks implements IConcreteBricks {}
