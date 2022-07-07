package gg.projecteden.nexus.features.customblocks.models.noteblocks.terracottashingles;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Green Terracotta Shingles",
	modelId = 20205
)
@CustomNoteBlockConfig(
	instrument = Instrument.BIT,
	step = 5,
	customBreakSound = "block.deepslate_bricks.break",
	customPlaceSound = "block.deepslate_bricks.place",
	customStepSound = "block.mud_bricks.step",
	customHitSound = "block.mud_bricks.hit",
	customFallSound = "block.mud_bricks.fall"
)
public class GreenTerracottaShingles implements IColoredTerracottaShingles {}
