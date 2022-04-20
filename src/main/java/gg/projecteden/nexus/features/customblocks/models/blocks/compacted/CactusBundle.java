package gg.projecteden.nexus.features.customblocks.models.blocks.compacted;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.annotations.DirectionalConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.ICompacted;
import gg.projecteden.nexus.features.customblocks.models.interfaces.IDirectional;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

@CustomBlockConfig(
	name = "Bundle of Cactus",
	modelId = 20061,
	instrument = Instrument.BASS_DRUM,
	step = 13
)
@DirectionalConfig(
	step_NS = 14,
	step_EW = 15
)
public class CactusBundle implements ICompacted, IDirectional {

	@Override
	public @NotNull Material getMaterial() {
		return Material.CACTUS;
	}
}
