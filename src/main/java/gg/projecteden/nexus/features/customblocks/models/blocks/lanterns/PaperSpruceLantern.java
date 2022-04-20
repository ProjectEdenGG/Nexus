package gg.projecteden.nexus.features.customblocks.models.blocks.lanterns;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.annotations.DirectionalConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.IPaperLantern;
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
