package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.quiltedwool;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Magenta Quilted Wool",
	itemModel = ItemModelType.QUILTED_WOOL_MAGENTA
)
@CustomNoteBlockConfig(
	instrument = Instrument.COW_BELL,
	step = 10,
	customBreakSound = "block.wool.break",
	customPlaceSound = "block.wool.place",
	customStepSound = "block.wool.step",
	customHitSound = "block.wool.hit",
	customFallSound = "block.wool.fall"
)
public class MagentaQuiltedWool implements IQuiltedWool {}
