package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.neon;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.utils.ColorType;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Yellow Neon Block",
	itemModel = ItemModelType.NEON_YELLOW
)
@CustomNoteBlockConfig(
	instrument = Instrument.SNARE_DRUM,
	step = 3,
	customBreakSound = "custom.block.stone.break",
	customPlaceSound = "custom.block.stone.place",
	customStepSound = "custom.block.stone.step",
	customHitSound = "custom.block.stone.hit",
	customFallSound = "custom.block.stone.fall"
)
public class YellowNeonBlock implements INeonBlock {

	@Override
	public ColorType getColor() {
		return ColorType.YELLOW;
	}
}
