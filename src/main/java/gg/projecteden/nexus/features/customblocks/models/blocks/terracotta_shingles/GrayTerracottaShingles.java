package gg.projecteden.nexus.features.customblocks.models.blocks.terracotta_shingles;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.dyeable.ITerracottaShingles;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Gray Terracotta Shingles",
	modelId = 20214,
	instrument = Instrument.BIT,
	step = 14
)
public class GrayTerracottaShingles implements ITerracottaShingles {}
