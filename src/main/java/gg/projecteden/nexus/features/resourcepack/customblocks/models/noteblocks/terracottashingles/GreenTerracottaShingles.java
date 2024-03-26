package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.terracottashingles;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
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
	customStepSound = "block.deepslate_bricks.step",
	customHitSound = "block.deepslate_bricks.hit",
	customFallSound = "block.deepslate_bricks.fall"
)
public class GreenTerracottaShingles implements IColoredTerracottaShingles {}
