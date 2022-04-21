package gg.projecteden.nexus.features.customblocks.models.blocks.terracottashingles;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Green Terracotta Shingles",
	modelId = 20205,
	instrument = Instrument.BIT,
	step = 5
)
public class GreenTerracottaShingles implements ITerracottaShingles {}
