package gg.projecteden.nexus.features.customblocks.models.blocks.lanterns;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.DirectionalConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Dark Oak Paper Lantern",
	modelId = 20406,
	instrument = Instrument.FLUTE,
	step = 16
)
@DirectionalConfig(
	step_NS = 17,
	step_EW = 18
)
public class DarkOakPaperLantern implements IPaperLantern {}
