package gg.projecteden.nexus.features.customblocks.models.blocks.terracottashingles;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Gray Terracotta Shingles",
	modelId = 20214,
	instrument = Instrument.BIT,
	step = 14
)
public class GrayTerracottaShingles implements IColoredTerracottaShingles {}
