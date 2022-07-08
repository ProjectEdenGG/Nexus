package gg.projecteden.nexus.features.customblocks.models.noteblocks.terracottashingles;

import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Light Gray Terracotta Shingles",
	modelId = 20215
)
@CustomNoteBlockConfig(
	instrument = Instrument.BIT,
	step = 15,
	customBreakSound = "block.deepslate_bricks.break",
	customPlaceSound = "block.deepslate_bricks.place",
	customStepSound = "block.mud_bricks.step",
	customHitSound = "block.mud_bricks.hit",
	customFallSound = "block.mud_bricks.fall"
)
public class LightGrayTerracottaShingles implements IColoredTerracottaShingles {}
