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
	customBreakSound = "block.stone.break",
	customPlaceSound = "block.stone.place",
	customStepSound = "block.stone.step",
	customHitSound = "block.stone.hit",
	customFallSound = "block.stone.fall"
)
public class DeepslatePillar implements IPillar {
	@Override
	public @NonNull Material getMaterial() {
		return Material.POLISHED_DEEPSLATE;
	}
}
