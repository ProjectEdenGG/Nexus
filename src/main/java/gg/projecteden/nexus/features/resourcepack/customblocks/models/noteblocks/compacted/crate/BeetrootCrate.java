package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.compacted.crate;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Crate of Beetroot",
	material = CustomMaterial.CRATES_CRATE_BEET
)
@CustomNoteBlockConfig(
	instrument = Instrument.BASS_DRUM,
	step = 2
)
public class BeetrootCrate implements ICrate {
}
