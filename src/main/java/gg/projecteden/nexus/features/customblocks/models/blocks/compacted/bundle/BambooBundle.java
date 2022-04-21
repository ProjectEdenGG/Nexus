package gg.projecteden.nexus.features.customblocks.models.blocks.compacted.bundle;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.DirectionalConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Bundle of Bamboo",
	modelId = 20060,
	instrument = Instrument.BASS_DRUM,
	step = 10
)
@DirectionalConfig(
	step_NS = 11,
	step_EW = 12
)
public class BambooBundle implements IBundle {}
