package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.neon;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Green Neon Block",
	itemModel = ItemModelType.NEON_GREEN
)
@CustomNoteBlockConfig(
	instrument = Instrument.SNARE_DRUM,
	step = 5,
	customBreakSound = "custom.block.stone.break",
	customPlaceSound = "custom.block.stone.place",
	customStepSound = "custom.block.stone.step",
	customHitSound = "custom.block.stone.hit",
	customFallSound = "custom.block.stone.fall"
)
public class GreenNeonBlock implements INeonBlock {
}
