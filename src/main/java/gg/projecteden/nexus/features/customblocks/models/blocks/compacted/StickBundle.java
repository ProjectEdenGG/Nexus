package gg.projecteden.nexus.features.customblocks.models.blocks.compacted;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.annotations.DirectionalConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.ICompacted;
import gg.projecteden.nexus.features.customblocks.models.interfaces.IDirectional;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

@CustomBlockConfig(
	name = "Bundle of Sticks",
	modelId = 20062,
	instrument = Instrument.BASS_DRUM,
	step = 16
)
@DirectionalConfig(
	step_NS = 17,
	step_EW = 18
)
public class StickBundle implements ICompacted, IDirectional {

	@Override
	public @NotNull Material getMaterial() {
		return Material.STICK;
	}
}
