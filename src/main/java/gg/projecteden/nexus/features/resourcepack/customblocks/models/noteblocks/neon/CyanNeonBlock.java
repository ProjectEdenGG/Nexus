package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.neon;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Cyan Neon Block",
	itemModel = ItemModelType.NEON_CYAN
)
@CustomNoteBlockConfig(
	instrument = Instrument.SNARE_DRUM,
	step = 6,
	customBreakSound = "custom.block.stone.break",
	customPlaceSound = "custom.block.stone.place",
	customStepSound = "custom.block.stone.step",
	customHitSound = "custom.block.stone.hit",
	customFallSound = "custom.block.stone.fall"
)
public class CyanNeonBlock implements INeonBlock {
}
