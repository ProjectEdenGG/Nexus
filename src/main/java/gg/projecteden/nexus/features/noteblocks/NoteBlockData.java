package gg.projecteden.nexus.features.noteblocks;

import gg.projecteden.nexus.utils.SoundBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Note;
import org.bukkit.SoundCategory;
import org.bukkit.block.data.type.NoteBlock;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoteBlockData {
	UUID placerUUID;
	NoteBlockInstrument instrument;
	Note note;
	double volume;

	public NoteBlockData of(NoteBlock noteBlock) {
		NoteBlockData data = new NoteBlockData();
		data.setInstrument(NoteBlockInstrument.of(noteBlock.getInstrument()));
		data.setNote(noteBlock.getNote());
		data.setVolume(1);
		return data;
	}

	public void play(Location location) {
		new SoundBuilder(this.instrument.getSound())
			.location(location)
			.pitchStep(this.note.getId())
			.volume(this.volume)
			.category(SoundCategory.RECORDS)
			.play();
	}
}
