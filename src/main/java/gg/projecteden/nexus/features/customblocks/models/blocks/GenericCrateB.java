package gg.projecteden.nexus.features.customblocks.models.blocks;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.ICustomBlock;
import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Generic Crate",
	modelId = 20102,
	instrument = Instrument.BASS_GUITAR,
	step = 2
)
public class GenericCrateB implements ICustomBlock {}
