package gg.projecteden.nexus.features.customblocks.models.blocks.compacted.bundle;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.annotations.DirectionalConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.IBundle;
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
