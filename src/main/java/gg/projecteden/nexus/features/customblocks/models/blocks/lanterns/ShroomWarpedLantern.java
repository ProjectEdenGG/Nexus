package gg.projecteden.nexus.features.customblocks.models.blocks.lanterns;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.annotations.DirectionalConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.IShroomLantern;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Warped Shroom Lantern",
	modelId = 20408,
	instrument = Instrument.FLUTE,
	step = 22
)
@DirectionalConfig(
	step_NS = 23,
	step_EW = 24
)
public class ShroomWarpedLantern implements IShroomLantern {}
