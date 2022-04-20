package gg.projecteden.nexus.features.customblocks.models.blocks.terracotta_shingles;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.ITerracottaShingles;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Lime Terracotta Shingles",
	modelId = 20204,
	instrument = Instrument.BIT,
	step = 4
)
public class LimeTerracottaShingles implements ITerracottaShingles {}
