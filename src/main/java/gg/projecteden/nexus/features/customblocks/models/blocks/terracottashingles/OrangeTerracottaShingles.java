package gg.projecteden.nexus.features.customblocks.models.blocks.terracottashingles;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.dyeable.ITerracottaShingles;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Orange Terracotta Shingles",
	modelId = 20202,
	instrument = Instrument.BIT,
	step = 2
)
public class OrangeTerracottaShingles implements ITerracottaShingles {}
