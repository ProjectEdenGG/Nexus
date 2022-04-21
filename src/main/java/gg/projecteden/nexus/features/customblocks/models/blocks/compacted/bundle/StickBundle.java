package gg.projecteden.nexus.features.customblocks.models.blocks.compacted.bundle;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.DirectionalConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Bundle of Sticks",
	modelId = 20062,
	instrument = Instrument.BASS_DRUM,
	step = 16
)
@DirectionalConfig(
	step_NS = 17,
	step_EW = 18
)
public class StickBundle implements IBundle {}
