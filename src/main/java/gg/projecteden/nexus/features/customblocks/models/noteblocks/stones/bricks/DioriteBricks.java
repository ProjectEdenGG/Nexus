package gg.projecteden.nexus.features.customblocks.models.noteblocks.stones.bricks;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Diorite Bricks",
	modelId = 20353
)
@CustomNoteBlockConfig(
	instrument = Instrument.DIDGERIDOO,
	step = 4
)
public class DioriteBricks implements IStoneBricks {}
