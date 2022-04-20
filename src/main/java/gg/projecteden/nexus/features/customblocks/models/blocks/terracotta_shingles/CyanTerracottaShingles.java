package gg.projecteden.nexus.features.customblocks.models.blocks.terracotta_shingles;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.dyeable.ITerracottaShingles;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Cyan Terracotta Shingles",
	modelId = 20206,
	instrument = Instrument.BIT,
	step = 6
)
public class CyanTerracottaShingles implements ITerracottaShingles {}
