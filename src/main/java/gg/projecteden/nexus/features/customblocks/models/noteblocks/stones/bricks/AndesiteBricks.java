package gg.projecteden.nexus.features.customblocks.models.noteblocks.stones.bricks;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Andesite Bricks",
	modelId = 20351
)
@CustomNoteBlockConfig(
	instrument = Instrument.DIDGERIDOO,
	step = 2
)
public class AndesiteBricks implements IStoneBricks {}
