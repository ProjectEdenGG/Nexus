package gg.projecteden.nexus.features.customblocks.models.blocks.terracottashingles;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Black Terracotta Shingles",
	modelId = 20213,
	instrument = Instrument.BIT,
	step = 13
)
public class BlackTerracottaShingles implements IColoredTerracottaShingles {
}
