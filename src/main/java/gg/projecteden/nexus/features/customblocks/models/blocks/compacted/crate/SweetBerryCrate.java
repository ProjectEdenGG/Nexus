package gg.projecteden.nexus.features.customblocks.models.blocks.compacted.crate;

import gg.projecteden.nexus.features.customblocks.models.blocks.common.annotations.CustomBlockConfig;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

@CustomBlockConfig(
	name = "Crate of Berries",
	modelId = 20053,
	instrument = Instrument.BASS_DRUM,
	step = 3
)
public class SweetBerryCrate implements ICrate {

	@Override
	public @NotNull Material getMaterial() {
		return Material.SWEET_BERRIES;
	}

}
