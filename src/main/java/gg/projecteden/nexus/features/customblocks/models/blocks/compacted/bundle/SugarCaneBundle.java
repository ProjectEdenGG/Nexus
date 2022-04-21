package gg.projecteden.nexus.features.customblocks.models.blocks.compacted.bundle;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.DirectionalConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Bundle of Sugar Cane",
	modelId = 20063,
	instrument = Instrument.BASS_DRUM,
	step = 19
)
@DirectionalConfig(
	step_NS = 20,
	step_EW = 21
)
public class SugarCaneBundle implements IBundle {}
