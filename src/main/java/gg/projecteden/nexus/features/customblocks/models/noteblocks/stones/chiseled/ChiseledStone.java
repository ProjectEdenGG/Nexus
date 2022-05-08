package gg.projecteden.nexus.features.customblocks.models.noteblocks.stones.chiseled;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

@CustomBlockConfig(
	name = "Chiseled Stone",
	modelId = 20350
)
@CustomNoteBlockConfig(
	instrument = Instrument.DIDGERIDOO,
	step = 1,
	customBreakSound = "block.stone.break",
	customPlaceSound = "block.stone.place",
	customStepSound = "block.stone.step",
	customHitSound = "block.stone.hit",
	customFallSound = "block.stone.fall"
)
public class ChiseledStone implements IChiseledStone {

	@Override
	public @NotNull Material getMaterial() {
		return Material.STONE_SLAB;
	}

}
