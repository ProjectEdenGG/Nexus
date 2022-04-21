package gg.projecteden.nexus.features.customblocks.models.blocks.terracottashingles;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Magenta Terracotta Shingles",
	modelId = 20210,
	instrument = Instrument.BIT,
	step = 10
)
public class MagentaTerracottaShingles implements IColoredTerracottaShingles {
}
