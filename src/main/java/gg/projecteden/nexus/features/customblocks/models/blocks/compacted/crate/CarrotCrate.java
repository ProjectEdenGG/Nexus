package gg.projecteden.nexus.features.customblocks.models.blocks.compacted.crate;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Crate of Carrots",
	modelId = 20054,
	instrument = Instrument.BASS_DRUM,
	step = 4
)
public class CarrotCrate implements ICrate {}
