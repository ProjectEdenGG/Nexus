package gg.projecteden.nexus.features.customblocks.models.blocks.terracotta_shingles;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.ITerracottaShingles;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Green Terracotta Shingles",
	modelId = 20205,
	instrument = Instrument.BIT,
	step = 5
)
public class GreenTerracottaShingles implements ITerracottaShingles {}
