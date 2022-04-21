package gg.projecteden.nexus.features.customblocks.models.blocks.terracottashingles;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Yellow Terracotta Shingles",
	modelId = 20203,
	instrument = Instrument.BIT,
	step = 3
)
public class YellowTerracottaShingles implements IColoredTerracottaShingles {}
