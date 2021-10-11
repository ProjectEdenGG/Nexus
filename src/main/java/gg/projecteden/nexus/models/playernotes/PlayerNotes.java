package gg.projecteden.nexus.models.playernotes;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.nexus.models.PlayerOwnedObject;
import lombok.*;
import me.lexikiq.HasUniqueId;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity(value = "player_notes", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class PlayerNotes implements PlayerOwnedObject {

	@Id
	@NonNull
	private UUID uuid;
	private List<PlayerNoteEntry> entries = new ArrayList<>();

	public void addEntry(HasUniqueId uuid, String note) {
		entries.add(new PlayerNoteEntry(note, uuid.getUniqueId()));
	}

	public void removeEntry(int index) {
		entries.remove(index);
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class PlayerNoteEntry {
		String note;
		UUID addedUser;
	}

}
