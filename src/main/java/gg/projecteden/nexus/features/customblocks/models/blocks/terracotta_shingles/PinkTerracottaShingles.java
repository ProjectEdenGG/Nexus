package gg.projecteden.nexus.features.customblocks.models.blocks.terracotta_shingles;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.ITerracottaShingles;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Pink Terracotta Shingles",
	modelId = 20211,
	instrument = Instrument.BIT,
	step = 11
)
public class PinkTerracottaShingles implements ITerracottaShingles {}
