package gg.projecteden.nexus.features.customblocks.models.blocks.terracotta_shingles;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.dyeable.ITerracottaShingles;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Yellow Terracotta Shingles",
	modelId = 20203,
	instrument = Instrument.BIT,
	step = 3
)
public class YellowTerracottaShingles implements ITerracottaShingles {}
