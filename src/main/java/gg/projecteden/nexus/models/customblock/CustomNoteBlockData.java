package gg.projecteden.nexus.models.customblock;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.ICustomNoteBlock;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.NoteBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
@NoArgsConstructor
public class CustomNoteBlockData extends ExtraBlockData {
	@NonNull BlockFace facing;
	NoteBlockData noteBlockData = null;

	public CustomNoteBlockData(@NotNull BlockFace facing) {
		this.facing = facing;
	}

	public @Nullable NoteBlockData getNoteBlockData(Block block, boolean reset) {
		if (this.noteBlockData == null) {
			if (reset) {
				NoteBlock noteBlock = (NoteBlock) Material.NOTE_BLOCK.createBlockData();
				ICustomNoteBlock customNoteBlock = (ICustomNoteBlock) CustomBlock.NOTE_BLOCK.get();

				noteBlock.setInstrument(customNoteBlock.getNoteBlockInstrument());
				noteBlock.setNote(new Note(customNoteBlock.getNoteBlockStep()));
				block.setBlockData(noteBlock, false);
			}
			this.noteBlockData = new NoteBlockData(block);
		}

		return this.noteBlockData;
	}

}
