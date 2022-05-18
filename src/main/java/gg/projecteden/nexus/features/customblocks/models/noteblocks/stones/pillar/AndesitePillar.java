package gg.projecteden.nexus.features.customblocks.models.noteblocks.stones.pillar;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

@CustomBlockConfig(
	name = "Andesite Pillar",
	modelId = 20354
)
@CustomNoteBlockConfig(
	instrument = Instrument.DIDGERIDOO,
	step = 5,
	customBreakSound = "block.stone.break",
	customPlaceSound = "block.stone.place",
	customStepSound = "block.stone.step",
	customHitSound = "block.stone.hit",
	customFallSound = "block.stone.fall"
)
public class AndesitePillar implements IPillar {
	@Override
	public @NotNull Material getMaterial() {
		return Material.POLISHED_ANDESITE;
	}
}
