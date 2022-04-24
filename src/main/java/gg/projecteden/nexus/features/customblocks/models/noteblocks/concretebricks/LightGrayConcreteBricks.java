package gg.projecteden.nexus.features.customblocks.models.noteblocks.concretebricks;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Light Gray Concrete Bricks",
	modelId = 20265
)
@CustomNoteBlockConfig(
	instrument = Instrument.CHIME,
	step = 15
)
public class LightGrayConcreteBricks implements IConcreteBricks {}
