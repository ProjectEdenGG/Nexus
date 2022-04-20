package gg.projecteden.nexus.features.customblocks.models.blocks.compacted;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.annotations.DirectionalConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.ICompacted;
import gg.projecteden.nexus.features.customblocks.models.interfaces.IDirectional;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

@CustomBlockConfig(
	name = "Bundle of Bamboo",
	modelId = 20060,
	instrument = Instrument.BASS_DRUM,
	step = 10
)
@DirectionalConfig(
	step_NS = 11,
	step_EW = 12
)
public class BambooBundle implements ICompacted, IDirectional {

	@Override
	public @NotNull Material getMaterial() {
		return Material.BAMBOO;
	}
}
