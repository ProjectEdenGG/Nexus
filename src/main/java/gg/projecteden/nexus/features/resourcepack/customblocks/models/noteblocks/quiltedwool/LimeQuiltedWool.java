package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.quiltedwool;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Lime Quilted Wool",
	material = CustomMaterial.QUILTED_WOOL_LIME
)
@CustomNoteBlockConfig(
	instrument = Instrument.COW_BELL,
	step = 4,
	customBreakSound = "block.wool.break",
	customPlaceSound = "block.wool.place",
	customStepSound = "block.wool.step",
	customHitSound = "block.wool.hit",
	customFallSound = "block.wool.fall"
)
public class LimeQuiltedWool implements IQuiltedWool {}
