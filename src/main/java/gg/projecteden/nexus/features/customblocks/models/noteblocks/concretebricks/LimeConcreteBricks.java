package gg.projecteden.nexus.features.customblocks.models.noteblocks.concretebricks;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Lime Concrete Bricks",
	modelId = 20254
)
@CustomNoteBlockConfig(
	instrument = Instrument.CHIME,
	step = 4
)
public class LimeConcreteBricks implements IConcreteBricks {}
