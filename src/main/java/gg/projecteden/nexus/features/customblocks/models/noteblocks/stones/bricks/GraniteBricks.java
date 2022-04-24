package gg.projecteden.nexus.features.customblocks.models.noteblocks.stones.bricks;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Diorite Bricks",
	modelId = 20355
)
@CustomNoteBlockConfig(
	instrument = Instrument.DIDGERIDOO,
	step = 6
)
public class GraniteBricks implements IStoneBricks {}
