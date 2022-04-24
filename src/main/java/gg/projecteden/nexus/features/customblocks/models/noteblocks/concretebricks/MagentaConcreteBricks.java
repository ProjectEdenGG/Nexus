package gg.projecteden.nexus.features.customblocks.models.noteblocks.concretebricks;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Magenta Concrete Bricks",
	modelId = 20260
)
@CustomNoteBlockConfig(
	instrument = Instrument.CHIME,
	step = 10
)
public class MagentaConcreteBricks implements IConcreteBricks {}
