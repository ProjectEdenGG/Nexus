package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.stones.chiseled;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

@CustomBlockConfig(
	name = "Chiseled Purpur",
	itemModel = ItemModelType.STONES_PURPUR_CHISELED_PURPUR
)
@CustomNoteBlockConfig(
	instrument = Instrument.DIDGERIDOO,
	step = 15,
	customBreakSound = "custom.block.stone.break",
	customPlaceSound = "custom.block.stone.place",
	customStepSound = "custom.block.stone.step",
	customHitSound = "custom.block.stone.hit",
	customFallSound = "custom.block.stone.fall"
)
public class ChiseledPurpur implements IChiseledStone {
	@Override
	public @NotNull Material getMaterial() {
		return Material.PURPUR_SLAB;
	}
}
