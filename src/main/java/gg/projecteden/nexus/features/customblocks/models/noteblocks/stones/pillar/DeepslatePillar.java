package gg.projecteden.nexus.features.customblocks.models.noteblocks.stones.pillar;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import lombok.NonNull;
import org.bukkit.Instrument;
import org.bukkit.Material;

@CustomBlockConfig(
	name = "Deepslate Pillar",
	modelId = 20362
)
@CustomNoteBlockConfig(
	instrument = Instrument.DIDGERIDOO,
	step = 13,
	customBreakSound = "custom.block.stone.break",
	customPlaceSound = "custom.block.stone.place",
	customStepSound = "custom.block.stone.step",
	customHitSound = "custom.block.stone.hit",
	customFallSound = "custom.block.stone.fall"
)
public class DeepslatePillar implements IPillar {
	@Override
	public @NonNull Material getMaterial() {
		return Material.POLISHED_DEEPSLATE;
	}
}
