package gg.projecteden.nexus.features.customblocks.models.noteblocks.stones.pillar;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import lombok.NonNull;
import org.bukkit.Instrument;
import org.bukkit.Material;

@CustomBlockConfig(
	name = "Diorite Pillar",
	modelId = 20357
)
@CustomNoteBlockConfig(
	instrument = Instrument.DIDGERIDOO,
	step = 8,
	customBreakSound = "custom.block.stone.break",
	customPlaceSound = "custom.block.stone.place",
	customStepSound = "custom.block.stone.step",
	customHitSound = "custom.block.stone.hit",
	customFallSound = "custom.block.stone.fall"
)
public class DioritePillar implements IPillar {
	@Override
	public @NonNull Material getMaterial() {
		return Material.POLISHED_DIORITE;
	}
}
