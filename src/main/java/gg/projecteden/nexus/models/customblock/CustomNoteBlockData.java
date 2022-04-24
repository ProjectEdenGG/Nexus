package gg.projecteden.nexus.models.customblock;

import gg.projecteden.nexus.features.customblocks.models.CustomBlock;
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
				noteBlock.setInstrument(CustomBlock.NOTE_BLOCK.getNoteBlock().getNoteBlockInstrument());
				noteBlock.setNote(new Note(CustomBlock.NOTE_BLOCK.getNoteBlock().getNoteBlockStep()));
				block.setBlockData(noteBlock, false);
			}
			this.noteBlockData = new NoteBlockData(block);
		}

		return this.noteBlockData;
	}

}
