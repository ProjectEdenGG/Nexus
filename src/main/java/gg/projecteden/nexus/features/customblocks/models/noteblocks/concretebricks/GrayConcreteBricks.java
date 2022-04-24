package gg.projecteden.nexus.features.customblocks.models.noteblocks.concretebricks;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Gray Concrete Bricks",
	modelId = 20264
)
@CustomNoteBlockConfig(
	instrument = Instrument.CHIME,
	step = 14
)
public class GrayConcreteBricks implements IConcreteBricks {}
