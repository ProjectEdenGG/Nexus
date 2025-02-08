package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.stones.chiseled;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

@CustomBlockConfig(
	name = "Chiseled Stone",
	material = CustomMaterial.STONES_STONE_CHISELED_STONE
)
@CustomNoteBlockConfig(
	instrument = Instrument.DIDGERIDOO,
	step = 1,
	customBreakSound = "custom.block.stone.break",
	customPlaceSound = "custom.block.stone.place",
	customStepSound = "custom.block.stone.step",
	customHitSound = "custom.block.stone.hit",
	customFallSound = "custom.block.stone.fall"
)
public class ChiseledStone implements IChiseledStone {

	@Override
	public @NotNull Material getMaterial() {
		return Material.STONE_SLAB;
	}

}
