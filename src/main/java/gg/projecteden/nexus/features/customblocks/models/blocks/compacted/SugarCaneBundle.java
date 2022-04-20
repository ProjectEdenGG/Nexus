package gg.projecteden.nexus.features.customblocks.models.blocks.compacted;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.annotations.DirectionalConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.ICompacted;
import gg.projecteden.nexus.features.customblocks.models.interfaces.IDirectional;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

@CustomBlockConfig(
	name = "Bundle of Sugar Cane",
	modelId = 20063,
	instrument = Instrument.BASS_DRUM,
	step = 19
)
@DirectionalConfig(
	step_NS = 20,
	step_EW = 21
)
public class SugarCaneBundle implements ICompacted, IDirectional {

	@Override
	public @NotNull Material getMaterial() {
		return Material.SUGAR_CANE;
	}
}
