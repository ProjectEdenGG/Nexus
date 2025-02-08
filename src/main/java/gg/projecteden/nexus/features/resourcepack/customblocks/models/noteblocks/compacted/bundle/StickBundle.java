package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.compacted.bundle;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.DirectionalConfig;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import org.bukkit.Instrument;

@CustomBlockConfig(
	name = "Bundle of Sticks",
	material = CustomMaterial.CRATES_BUNDLE_STICK
)
@CustomNoteBlockConfig(
	instrument = Instrument.BASS_DRUM,
	step = 16
)
@DirectionalConfig(
	step_NS = 17,
	step_EW = 18
)
public class StickBundle implements IBundle {
}
