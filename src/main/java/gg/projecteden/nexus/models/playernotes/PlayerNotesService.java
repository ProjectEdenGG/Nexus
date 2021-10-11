package gg.projecteden.nexus.models.playernotes;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(PlayerNotes.class)
public class PlayerNotesService extends MongoService<PlayerNotes> {

	private final static Map<UUID, PlayerNotes> cache = new ConcurrentHashMap<>();

	@Override
	public Map<UUID, PlayerNotes> getCache() {
		return cache;
	}

	public List<PlayerNotes> getByKeyword(String keyword) {
		List<PlayerNotes> notes = new ArrayList<>();
		Notes:
		for (PlayerNotes playerNotes : getAll()) {
			for (PlayerNotes.PlayerNoteEntry entry : playerNotes.getEntries()) {
				if (entry.getNote().contains(keyword)) {
					notes.add(playerNotes);
					continue Notes;
				}
			}
		}
		return notes;
	}

}
