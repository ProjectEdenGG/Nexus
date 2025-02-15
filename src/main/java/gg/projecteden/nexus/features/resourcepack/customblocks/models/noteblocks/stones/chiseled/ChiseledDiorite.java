package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.stones.chiseled;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Chiseled Diorite",
	itemModel = ItemModelType.STONES_DIORITE_CHISELED_DIORITE
)
@CustomNoteBlockConfig(
	instrument = Instrument.DIDGERIDOO,
	step = 7,
	customBreakSound = "custom.block.stone.break",
	customPlaceSound = "custom.block.stone.place",
	customStepSound = "custom.block.stone.step",
	customHitSound = "custom.block.stone.hit",
	customFallSound = "custom.block.stone.fall"
)
public class ChiseledDiorite implements IChiseledStone {
}
