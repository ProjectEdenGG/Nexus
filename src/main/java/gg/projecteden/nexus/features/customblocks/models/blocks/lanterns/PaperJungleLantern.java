package gg.projecteden.nexus.features.customblocks.models.blocks.lanterns;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.annotations.DirectionalConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.IPaperLantern;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Jungle Paper Lantern",
	modelId = 20404,
	instrument = Instrument.FLUTE,
	step = 10
)
@DirectionalConfig(
	step_NS = 11,
	step_EW = 12
)
public class PaperJungleLantern implements IPaperLantern {}
