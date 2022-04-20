package gg.projecteden.nexus.features.customblocks.models.blocks.quilted_wool;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.IQuiltedWool;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

@CustomBlockConfig(
	name = "Magenta Quilted Wool",
	modelId = 20310,
	instrument = Instrument.COW_BELL,
	step = 10
)
public class MagentaQuiltedWool implements IQuiltedWool {

	@Override
	public @NotNull Material getWool() {
		return Material.MAGENTA_WOOL;
	}
}
