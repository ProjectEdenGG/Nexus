package gg.projecteden.nexus.features.customblocks.models.blocks.lanterns;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.DirectionalConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Crimson Shroom Lantern",
	modelId = 20407,
	instrument = Instrument.FLUTE,
	step = 19
)
@DirectionalConfig(
	step_NS = 20,
	step_EW = 21
)
public class ShroomCrimsonLantern implements IShroomLantern {}
