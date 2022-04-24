package gg.projecteden.nexus.features.customblocks.models.noteblocks.terracottashingles;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;
import org.bukkit.Material;

@CustomBlockConfig(
	name = "Terracotta Shingles",
	modelId = 20217
)
@CustomNoteBlockConfig(
	instrument = Instrument.BIT,
	step = 17
)
public class TerracottaShingles implements ITerracottaShingles {

	@Override
	public Material getMaterial() {
		return Material.TERRACOTTA;
	}

}
