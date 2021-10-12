package gg.projecteden.nexus.models.playernotes;

import dev.morphia.query.Query;
import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

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
		Query<PlayerNotes> query = database.createQuery(PlayerNotes.class);
		query.and(query.criteria("entries.note").containsIgnoreCase(keyword));
		return query.find().toList();
	}

}
