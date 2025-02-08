package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.terracottashingles;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Cyan Terracotta Shingles",
	material = CustomMaterial.TERRACOTTA_SHINGLES_CYAN
)
@CustomNoteBlockConfig(
	instrument = Instrument.BIT,
	step = 6,
	customBreakSound = "block.deepslate_bricks.break",
	customPlaceSound = "block.deepslate_bricks.place",
	customStepSound = "block.deepslate_bricks.step",
	customHitSound = "block.deepslate_bricks.hit",
	customFallSound = "block.deepslate_bricks.fall"
)
public class CyanTerracottaShingles implements IColoredTerracottaShingles {}
