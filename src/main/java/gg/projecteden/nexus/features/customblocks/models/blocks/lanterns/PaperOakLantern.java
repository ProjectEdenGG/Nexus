package gg.projecteden.nexus.features.customblocks.models.blocks.lanterns;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.DirectionalConfig;
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
