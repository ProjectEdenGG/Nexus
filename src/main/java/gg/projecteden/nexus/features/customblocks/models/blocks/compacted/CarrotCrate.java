package gg.projecteden.nexus.features.customblocks.models.blocks.compacted;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.ICompacted;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

@CustomBlockConfig(
	name = "Crate of Carrots",
	modelId = 20054,
	instrument = Instrument.BASS_DRUM,
	step = 4
)
public class CarrotCrate implements ICompacted {

	@Override
	public @NotNull Material getMaterial() {
		return Material.CARROT;
	}
}
