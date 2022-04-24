package gg.projecteden.nexus.features.customblocks.models.noteblocks.concretebricks;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Pink Concrete Bricks",
	modelId = 20261
)
@CustomNoteBlockConfig(
	instrument = Instrument.CHIME,
	step = 11
)
public class PinkConcreteBricks implements IConcreteBricks {}
