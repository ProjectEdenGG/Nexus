package gg.projecteden.nexus.features.customblocks.models.noteblocks.concretebricks;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Light Blue Concrete Bricks",
	modelId = 20257
)
@CustomNoteBlockConfig(
	instrument = Instrument.CHIME,
	step = 7
)
public class LightBlueConcreteBricks implements IConcreteBricks {}
