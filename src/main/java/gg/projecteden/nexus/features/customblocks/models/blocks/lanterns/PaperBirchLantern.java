package gg.projecteden.nexus.features.customblocks.models.blocks.lanterns;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.annotations.DirectionalConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.IPaperLantern;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Birch Paper Lantern",
	modelId = 20403,
	instrument = Instrument.FLUTE,
	step = 7
)
@DirectionalConfig(
	step_NS = 8,
	step_EW = 9
)
public class PaperBirchLantern implements IPaperLantern {}
