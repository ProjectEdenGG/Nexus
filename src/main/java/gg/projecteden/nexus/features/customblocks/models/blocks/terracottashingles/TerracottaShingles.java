package gg.projecteden.nexus.features.customblocks.models.blocks.terracottashingles;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;
import org.bukkit.Material;

@CustomBlockConfig(
	name = "Terracotta Shingles",
	modelId = 20217,
	instrument = Instrument.BIT,
	step = 17
)
public class TerracottaShingles implements ITerracottaShingles {

	@Override
	public Material getMaterial() {
		return Material.TERRACOTTA;
	}
}
