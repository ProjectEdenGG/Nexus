package gg.projecteden.nexus.features.customblocks.models.blocks.terracotta_shingles;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.dyeable.ITerracottaShingles;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Magenta Terracotta Shingles",
	modelId = 20210,
	instrument = Instrument.BIT,
	step = 10
)
public class MagentaTerracottaShingles implements ITerracottaShingles {}
