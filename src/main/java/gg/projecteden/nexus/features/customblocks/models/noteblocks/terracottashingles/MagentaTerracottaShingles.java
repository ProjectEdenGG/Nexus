package gg.projecteden.nexus.features.customblocks.models.noteblocks.terracottashingles;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Magenta Terracotta Shingles",
	modelId = 20210
)
@CustomNoteBlockConfig(
	instrument = Instrument.BIT,
	step = 10,
	customBreakSound = "block.deepslate_bricks.break",
	customPlaceSound = "block.deepslate_bricks.place",
	customStepSound = "block.deepslate_bricks.step",
	customHitSound = "block.deepslate_bricks.hit",
	customFallSound = "block.deepslate_bricks.fall"
)
public class MagentaTerracottaShingles implements IColoredTerracottaShingles {}
