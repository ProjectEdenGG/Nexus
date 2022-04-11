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

import java.util.UUID;

@Data
@NoArgsConstructor
public class CustomBlockData {
	UUID placerUUID = null;
	int modelData;
	@NonNull BlockFace facing;
	NoteBlockData noteBlockData = null;

	public CustomBlockData(UUID uuid, int modelData, @NotNull BlockFace facing) {
		this.placerUUID = uuid;
		this.modelData = modelData;
		this.facing = facing;
	}

	public boolean exists() {
		return this.placerUUID != null && getCustomBlock() != null;
	}

	public boolean isNoteBlock() {
		return this.noteBlockData != null;
	}

	public @Nullable CustomBlock getCustomBlock() {
		return CustomBlock.fromModelData(modelData);
	}

	public @Nullable NoteBlockData getNoteBlockData(Block block, boolean reset) {
		if (this.noteBlockData == null) {
			if (reset) {
				NoteBlock noteBlock = (NoteBlock) Material.NOTE_BLOCK.createBlockData();
				noteBlock.setInstrument(CustomBlock.NOTE_BLOCK.get().getNoteBlockInstrument());
				noteBlock.setNote(new Note(CustomBlock.NOTE_BLOCK.get().getNoteBlockStep()));
				block.setBlockData(noteBlock, false);
			}
			this.noteBlockData = new NoteBlockData(block);
		}

		return this.noteBlockData;
	}

}
