package gg.projecteden.nexus.features.customblocks.models.blocks.quilted_wool;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.IQuiltedWool;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

@CustomBlockConfig(
	name = "Light Blue Quilted Wool",
	modelId = 20307,
	instrument = Instrument.COW_BELL,
	step = 7
)
public class LightBlueQuiltedWool implements IQuiltedWool {

	@Override
	public @NotNull Material getWool() {
		return Material.LIGHT_BLUE_WOOL;
	}
}
