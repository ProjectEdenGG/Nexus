package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.quiltedwool;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Blue Quilted Wool",
	itemModel = ItemModelType.QUILTED_WOOL_BLUE
)
@CustomNoteBlockConfig(
	instrument = Instrument.COW_BELL,
	step = 8,
	customBreakSound = "block.wool.break",
	customPlaceSound = "block.wool.place",
	customStepSound = "block.wool.step",
	customHitSound = "block.wool.hit",
	customFallSound = "block.wool.fall"
)
public class BlueQuiltedWool implements IQuiltedWool {}
