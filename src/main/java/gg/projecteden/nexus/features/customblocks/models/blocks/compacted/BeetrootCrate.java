package gg.projecteden.nexus.features.customblocks.models.blocks.compacted;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.ICompacted;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

@CustomBlockConfig(
	name = "Crate of Beetroot",
	modelId = 20052,
	instrument = Instrument.BASS_DRUM,
	step = 2
)
public class BeetrootCrate implements ICompacted {

	@Override
	public @NotNull Material getMaterial() {
		return Material.BEETROOT;
	}
}
