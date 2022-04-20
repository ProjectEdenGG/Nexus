package gg.projecteden.nexus.features.customblocks.models.blocks.quilted_wool;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.IQuiltedWool;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

@CustomBlockConfig(
	name = "Lime Quilted Wool",
	modelId = 20304,
	instrument = Instrument.COW_BELL,
	step = 4
)
public class LimeQuiltedWool implements IQuiltedWool {

	@Override
	public @NotNull Material getWool() {
		return Material.LIME_WOOL;
	}
}
