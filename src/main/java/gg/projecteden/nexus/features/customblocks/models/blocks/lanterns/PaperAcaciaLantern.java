package gg.projecteden.nexus.features.customblocks.models.blocks.lanterns;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.DirectionalConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Acacia Paper Lantern",
	modelId = 20405,
	instrument = Instrument.FLUTE,
	step = 13
)
@DirectionalConfig(
	step_NS = 14,
	step_EW = 15
)
public class PaperAcaciaLantern implements IPaperLantern {}
