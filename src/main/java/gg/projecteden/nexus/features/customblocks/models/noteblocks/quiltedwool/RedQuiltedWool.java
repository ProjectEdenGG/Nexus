package gg.projecteden.nexus.features.customblocks.models.noteblocks.quiltedwool;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Red Quilted Wool",
	modelId = 20301
)
@CustomNoteBlockConfig(
	instrument = Instrument.COW_BELL,
	step = 1,
	customBreakSound = "block.wool.break",
	customPlaceSound = "block.wool.place",
	customStepSound = "block.wool.step",
	customHitSound = "block.wool.hit",
	customFallSound = "block.wool.fall"
)
public class RedQuiltedWool implements IQuiltedWool {}
