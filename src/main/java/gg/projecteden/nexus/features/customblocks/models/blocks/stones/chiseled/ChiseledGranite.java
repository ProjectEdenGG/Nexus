package gg.projecteden.nexus.features.customblocks.models.blocks.stones.chiseled;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.IChiseledBricks;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Chiseled Granite",
	modelId = 20356,
	instrument = Instrument.DIDGERIDOO,
	step = 7
)
public class ChiseledGranite implements IChiseledBricks {}
