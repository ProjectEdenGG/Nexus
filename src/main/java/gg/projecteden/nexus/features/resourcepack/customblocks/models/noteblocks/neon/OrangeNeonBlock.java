package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.neon;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.utils.ColorType;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Orange Neon Block",
	itemModel = ItemModelType.NEON_ORANGE
)
@CustomNoteBlockConfig(
	instrument = Instrument.SNARE_DRUM,
	step = 2,
	customBreakSound = "custom.block.stone.break",
	customPlaceSound = "custom.block.stone.place",
	customStepSound = "custom.block.stone.step",
	customHitSound = "custom.block.stone.hit",
	customFallSound = "custom.block.stone.fall"
)
public class OrangeNeonBlock implements INeonBlock {

	@Override
	public ColorType getColor() {
		return ColorType.ORANGE;
	}
}
