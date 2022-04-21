package gg.projecteden.nexus.features.customblocks.models.blocks;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.ICustomBlock;
import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Generic Crate",
	modelId = 20101,
	instrument = Instrument.BASS_GUITAR,
	step = 1
)
public class GenericCrateA implements ICustomBlock {}
