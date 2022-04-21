package gg.projecteden.nexus.features.customblocks.models.blocks.terracottashingles;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Brown Terracotta Shingles",
	modelId = 20212,
	instrument = Instrument.BIT,
	step = 12
)
public class BrownTerracottaShingles implements IColoredTerracottaShingles {
}
