package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.stones.pillar;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

@CustomBlockConfig(
	name = "Andesite Pillar",
	itemModel = ItemModelType.STONES_ANDESITE_ANDESITE_PILLAR
)
@CustomNoteBlockConfig(
	instrument = Instrument.DIDGERIDOO,
	step = 5,
	customBreakSound = "custom.block.stone.break",
	customPlaceSound = "custom.block.stone.place",
	customStepSound = "custom.block.stone.step",
	customHitSound = "custom.block.stone.hit",
	customFallSound = "custom.block.stone.fall"
)
public class AndesiteStonePillar implements IStonePillar {
	@Override
	public @NotNull Material getMaterial() {
		return Material.POLISHED_ANDESITE;
	}
}
