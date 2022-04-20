package gg.projecteden.nexus.features.customblocks.models.blocks;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.ICustomBlock;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Generic Crate",
	modelId = 20104,
	instrument = Instrument.BASS_GUITAR,
	step = 4
)
public class GenericCrateD implements ICustomBlock {}
