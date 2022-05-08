package gg.projecteden.nexus.features.customblocks.models.noteblocks.terracottashingles;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Lime Terracotta Shingles",
	modelId = 20204
)
@CustomNoteBlockConfig(
	instrument = Instrument.BIT,
	step = 4,
	customBreakSound = "block.stone.break",
	customPlaceSound = "block.stone.place",
	customStepSound = "block.stone.step",
	customHitSound = "block.stone.hit",
	customFallSound = "block.stone.fall"
)
public class LimeTerracottaShingles implements IColoredTerracottaShingles {}
