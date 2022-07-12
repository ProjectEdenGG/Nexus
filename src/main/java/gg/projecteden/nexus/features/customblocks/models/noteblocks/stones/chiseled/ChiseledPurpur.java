package gg.projecteden.nexus.features.customblocks.models.noteblocks.stones.chiseled;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Chiseled Purpur",
	modelId = 20364
)
@CustomNoteBlockConfig(
	instrument = Instrument.DIDGERIDOO,
	step = 15,
	customBreakSound = "custom.block.stone.break",
	customPlaceSound = "custom.block.stone.place",
	customStepSound = "custom.block.stone.step",
	customHitSound = "custom.block.stone.hit",
	customFallSound = "custom.block.stone.fall"
)
public class ChiseledPurpur implements IChiseledStone {
}
