package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.terracottashingles;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Lime Terracotta Shingles",
	itemModel = ItemModelType.TERRACOTTA_SHINGLES_LIME
)
@CustomNoteBlockConfig(
	instrument = Instrument.BIT,
	step = 4,
	customBreakSound = "block.deepslate_bricks.break",
	customPlaceSound = "block.deepslate_bricks.place",
	customStepSound = "block.deepslate_bricks.step",
	customHitSound = "block.deepslate_bricks.hit",
	customFallSound = "block.deepslate_bricks.fall"
)
public class LimeTerracottaShingles implements IColoredTerracottaShingles {}
