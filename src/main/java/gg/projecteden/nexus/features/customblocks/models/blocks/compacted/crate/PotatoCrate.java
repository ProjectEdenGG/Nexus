package gg.projecteden.nexus.features.customblocks.models.blocks.compacted.crate;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Crate of Potatoes",
	modelId = 20055,
	instrument = Instrument.BASS_DRUM,
	step = 5
)
public class PotatoCrate implements ICrate {}
