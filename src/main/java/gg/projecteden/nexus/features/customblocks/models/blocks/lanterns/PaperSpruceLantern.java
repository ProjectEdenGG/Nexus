package gg.projecteden.nexus.features.customblocks.models.blocks.lanterns;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.DirectionalConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Spruce Paper Lantern",
	modelId = 20402,
	instrument = Instrument.FLUTE,
	step = 4
)
@DirectionalConfig(
	step_NS = 5,
	step_EW = 6
)
public class PaperSpruceLantern implements IPaperLantern {}
