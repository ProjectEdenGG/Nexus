package gg.projecteden.nexus.features.customblocks.models.blocks.terracotta_shingles;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.ITerracottaShingles;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Red Terracotta Shingles",
	modelId = 20201,
	instrument = Instrument.BIT,
	step = 1
)
public class RedTerracottaShingles implements ITerracottaShingles {}
