package gg.projecteden.nexus.features.customblocks.models.blocks.stones.chiseled;

import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.IChiseledBricks;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

@CustomBlockConfig(
	name = "Chiseled Stone",
	modelId = 20350,
	instrument = Instrument.DIDGERIDOO,
	step = 1
)
public class ChiseledStone implements IChiseledBricks {

	@Override
	public @NotNull Material getMaterial() {
		return Material.STONE_SLAB;
	}

}
