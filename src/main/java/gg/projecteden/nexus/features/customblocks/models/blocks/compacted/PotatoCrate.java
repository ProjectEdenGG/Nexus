package gg.projecteden.nexus.features.customblocks.models.blocks.compacted;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.ICompacted;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

@CustomBlockConfig(
	name = "Crate of Potatoes",
	modelId = 20055,
	instrument = Instrument.BASS_DRUM,
	step = 5
)
public class PotatoCrate implements ICompacted {

	@Override
	public @NotNull Material getMaterial() {
		return Material.POTATO;
	}
}
