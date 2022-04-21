package gg.projecteden.nexus.features.customblocks.models.blocks.lanterns;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.DirectionalConfig;
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
