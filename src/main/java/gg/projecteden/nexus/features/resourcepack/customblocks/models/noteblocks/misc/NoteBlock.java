package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.misc;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.CustomNoteBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.ICustomNoteBlock;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.NonNull;
import org.bukkit.Instrument;
import org.bukkit.Material;

@CustomBlockConfig(
	name = "Note Block",
	material = CustomMaterial.NOTE_BLOCK
)
@CustomNoteBlockConfig(
	instrument = Instrument.PIANO,
	step = 0
)
public class NoteBlock implements ICustomNoteBlock {

	@Override
	public @NonNull ItemBuilder getItemBuilder() {
		return new ItemBuilder(Material.NOTE_BLOCK);
	}

	@Override
	public double getBlockHardness() {
		return 0.8;
	}

	@Override
	public boolean requiresCorrectToolForDrops() {
		return false;
	}

	@Override
	public Material getMinimumPreferredTool() {
		return Material.WOODEN_AXE;
	}
}
