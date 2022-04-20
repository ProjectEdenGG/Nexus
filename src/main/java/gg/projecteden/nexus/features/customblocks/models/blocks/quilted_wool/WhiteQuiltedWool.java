package gg.projecteden.nexus.features.customblocks.models.blocks.quilted_wool;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.IQuiltedWool;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

@CustomBlockConfig(
	name = "White Quilted Wool",
	modelId = 20316,
	instrument = Instrument.COW_BELL,
	step = 16
)
public class WhiteQuiltedWool implements IQuiltedWool {

	@Override
	public @NotNull Material getWool() {
		return Material.WHITE_WOOL;
	}
}
