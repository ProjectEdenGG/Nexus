package gg.projecteden.nexus.features.customblocks.models.blocks.terracotta_shingles;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.dyeable.ITerracottaShingles;
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
