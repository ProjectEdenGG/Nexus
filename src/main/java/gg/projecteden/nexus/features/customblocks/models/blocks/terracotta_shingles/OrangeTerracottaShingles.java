package gg.projecteden.nexus.features.customblocks.models.blocks.terracotta_shingles;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.ITerracottaShingles;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Orange Terracotta Shingles",
	modelId = 20202,
	instrument = Instrument.BIT,
	step = 2
)
public class OrangeTerracottaShingles implements ITerracottaShingles {}
