package gg.projecteden.nexus.features.customblocks.models.blocks.compacted.crate;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.ICrate;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Crate of Beetroot",
	modelId = 20052,
	instrument = Instrument.BASS_DRUM,
	step = 2
)
public class BeetrootCrate implements ICrate {}
