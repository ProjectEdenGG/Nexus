package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.stones.pillar;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import lombok.NonNull;
import org.bukkit.Instrument;
import org.bukkit.Material;

@CustomBlockConfig(
	name = "Deepslate Pillar",
	itemModel = ItemModelType.STONES_DEEPSLATE_DEEPSLATE_PILLAR
)
@CustomNoteBlockConfig(
	instrument = Instrument.DIDGERIDOO,
	step = 13,
	customBreakSound = "block.deepslate_bricks.break",
	customPlaceSound = "block.deepslate_bricks.place",
	customStepSound = "block.deepslate_bricks.step",
	customHitSound = "block.deepslate_bricks.hit",
	customFallSound = "block.deepslate_bricks.fall"
)
public class DeepslateStonePillar implements IStonePillar {
	@Override
	public @NonNull Material getMaterial() {
		return Material.POLISHED_DEEPSLATE;
	}
}
