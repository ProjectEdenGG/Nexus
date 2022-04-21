package gg.projecteden.nexus.features.customblocks.models.blocks.compacted.bundle;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.DirectionalConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Bundle of Cactus",
	modelId = 20061,
	instrument = Instrument.BASS_DRUM,
	step = 13
)
@DirectionalConfig(
	step_NS = 14,
	step_EW = 15
)
public class CactusBundle implements IBundle {}
