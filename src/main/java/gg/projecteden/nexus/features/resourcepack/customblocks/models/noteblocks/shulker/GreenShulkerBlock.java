package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.shulker;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Green Shulker Block",
	itemModel = ItemModelType.SHULKER_GREEN
)
@CustomNoteBlockConfig(
	instrument = Instrument.XYLOPHONE,
	step = 5,
	customBreakSound = "custom.block.stone.break",
	customPlaceSound = "custom.block.stone.place",
	customStepSound = "custom.block.stone.step",
	customHitSound = "custom.block.stone.hit",
	customFallSound = "custom.block.stone.fall"
)
public class GreenShulkerBlock implements IShulkerBlock {
}
