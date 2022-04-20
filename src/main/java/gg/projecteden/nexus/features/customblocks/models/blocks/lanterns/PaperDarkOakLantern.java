package gg.projecteden.nexus.features.customblocks.models.blocks.lanterns;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.annotations.DirectionalConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.IPaperLantern;
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
public class PaperDarkOakLantern implements IPaperLantern {}
