package gg.projecteden.nexus.features.customblocks.models.blocks.lanterns;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.annotations.DirectionalConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.IPaperLantern;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Oak Paper Lantern",
	modelId = 20401,
	instrument = Instrument.FLUTE,
	step = 1
)
@DirectionalConfig(
	step_NS = 2,
	step_EW = 3
)
public class PaperOakLantern implements IPaperLantern {}
