package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.stones.pillar;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import lombok.NonNull;
import org.bukkit.Instrument;
import org.bukkit.Material;

@CustomBlockConfig(
	name = "Stone Pillar",
	itemModel = ItemModelType.STONES_STONE_STONE_PILLAR
)
@CustomNoteBlockConfig(
	instrument = Instrument.DIDGERIDOO,
	step = 2,
	customBreakSound = "custom.block.stone.break",
	customPlaceSound = "custom.block.stone.place",
	customStepSound = "custom.block.stone.step",
	customHitSound = "custom.block.stone.hit",
	customFallSound = "custom.block.stone.fall"
)
public class StoneStonePillar implements IStonePillar {
	@Override
	public @NonNull Material getMaterial() {
		return Material.STONE;
	}
}
