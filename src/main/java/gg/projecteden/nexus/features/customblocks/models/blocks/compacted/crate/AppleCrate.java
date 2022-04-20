package gg.projecteden.nexus.features.customblocks.models.blocks.compacted.crate;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.ICrate;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Crate of Apples",
	modelId = 20051,
	instrument = Instrument.BASS_DRUM,
	step = 1
)
public class AppleCrate implements ICrate {}
