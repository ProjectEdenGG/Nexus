package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.compacted.crate;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

@CustomBlockConfig(
	name = "Crate of Berries",
	modelId = 20053
)
@CustomNoteBlockConfig(
	instrument = Instrument.BASS_DRUM,
	step = 3
)
public class SweetBerryCrate implements ICrate {

	@Override
	public @NotNull Material getMaterial() {
		return Material.SWEET_BERRIES;
	}

}
